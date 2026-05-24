package pt.ubi.gruposd.loja.exception;

// Lançada quando o pedido do cliente é sintaticamente válido mas viola uma regra de negócio, como stock insuficiente ou produto inativo, e é convertida em HTTP 400 Bad Request pelo ApiExceptionHandler.
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}

