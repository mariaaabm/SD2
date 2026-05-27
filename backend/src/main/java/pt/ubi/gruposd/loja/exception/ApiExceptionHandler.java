package pt.ubi.gruposd.loja.exception;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Tratamento centralizado de exceções para todos os controllers da API.
// @RestControllerAdvice intercepta exceções lançadas em qualquer controller e devolve
// sempre o mesmo formato JSON — ApiError com timestamp, status, nome do erro e mensagens —
// para que o frontend possa processar erros de forma uniforme sem verificar formatos diferentes.
@RestControllerAdvice
public class ApiExceptionHandler {

    // DTO de resposta de erro — record imutável com os campos que o frontend espera no campo "messages".
    public record ApiError(LocalDateTime timestamp, int status, String error, List<String> messages) {}

    // Recurso não encontrado → HTTP 404
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException exception) {
        return build(HttpStatus.NOT_FOUND, List.of(exception.getMessage()));
    }

    // Conflito de dados (email ou categoria já existem) → HTTP 409
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException exception) {
        return build(HttpStatus.CONFLICT, List.of(exception.getMessage()));
    }

    // Autenticação inválida ou sessão expirada → HTTP 401
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException exception) {
        return build(HttpStatus.UNAUTHORIZED, List.of(exception.getMessage()));
    }

    // Violação de regra de negócio (stock esgotado, produto inativo) → HTTP 400
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException exception) {
        return build(HttpStatus.BAD_REQUEST, List.of(exception.getMessage()));
    }

    // Erros de validação do Bean Validation (@NotNull, @Size, etc.) → HTTP 400 com lista de campos inválidos.
    // Devolve múltiplas mensagens numa só resposta para que o frontend possa mostrar todos os erros de uma vez.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        List<String> messages = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .toList();
        return build(HttpStatus.BAD_REQUEST, messages);
    }

    // Argumento inválido lançado explicitamente no código da aplicação (ex.: período desconhecido no StatsService).
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException exception) {
        return build(HttpStatus.BAD_REQUEST, List.of(exception.getMessage()));
    }

    // Constrói o envelope de resposta de erro com o timestamp do momento exato do erro para facilitar o diagnóstico em logs.
    private ResponseEntity<ApiError> build(HttpStatus status, List<String> messages) {
        return ResponseEntity.status(status).body(
            new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(), messages)
        );
    }
}
