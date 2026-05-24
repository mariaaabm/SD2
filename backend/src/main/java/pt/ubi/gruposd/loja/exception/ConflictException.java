package pt.ubi.gruposd.loja.exception;

// Lançada quando a operação colide com o estado atual da base de dados, como tentar criar uma categoria com nome já existente ou registar um email duplicado, e é convertida em HTTP 409 Conflict pelo ApiExceptionHandler.
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}

