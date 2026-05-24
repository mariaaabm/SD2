package pt.ubi.gruposd.loja.dto;

import java.time.LocalDateTime;

// Vista de uma review devolvida pela API com o nome do cliente já resolvido para o frontend mostrar a autoria sem ter de fazer um pedido extra.
public record ReviewResponse(
    Long id,
    Long customerId,
    String customerName,
    Long productId,
    Integer rating,
    String comment,
    LocalDateTime createdAt
) {}
