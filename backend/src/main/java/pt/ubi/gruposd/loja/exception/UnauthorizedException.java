package pt.ubi.gruposd.loja.exception;

// Lançada quando o utilizador não está autenticado ou a credencial é inválida,
// como password atual errada ou refresh token expirado.
// É convertida em HTTP 401 Unauthorized pelo ApiExceptionHandler.
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
