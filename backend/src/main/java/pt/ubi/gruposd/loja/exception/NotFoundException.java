package pt.ubi.gruposd.loja.exception;

// Lançada quando o recurso pedido não existe na base de dados, como um produto, categoria ou venda inexistente, e é convertida em HTTP 404 Not Found pelo ApiExceptionHandler.
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

