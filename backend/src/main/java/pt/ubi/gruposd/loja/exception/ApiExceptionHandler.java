package pt.ubi.gruposd.loja.exception;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Interceta exceções de qualquer controller e converte-as em respostas JSON uniformes.
// Assim o frontend recebe sempre o mesmo formato de erro, independentemente do que correu mal.
@RestControllerAdvice
public class ApiExceptionHandler {

    // Formato de erro devolvido pela API: timestamp, código HTTP, nome do erro e lista de mensagens.
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

    // Erros de validação de campos (@NotNull, @Size, etc.) → HTTP 400 com a lista de todos os campos inválidos.
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

    // Monta a resposta de erro com o timestamp, código HTTP, nome e mensagens.
    private ResponseEntity<ApiError> build(HttpStatus status, List<String> messages) {
        return ResponseEntity.status(status).body(
            new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(), messages)
        );
    }
}
