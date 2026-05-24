package pt.ubi.gruposd.loja.exception;

import java.time.LocalDateTime;
import java.util.List;

// Formato uniforme de resposta de erro da API com timestamp, código HTTP, descrição e lista de mensagens, permitindo que o frontend trate erros de forma consistente independentemente do endpoint que falhou.
public record ApiError(
    LocalDateTime timestamp,
    int status,
    String error,
    List<String> messages
) {
}

