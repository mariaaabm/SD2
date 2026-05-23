package pt.ubi.gruposd.loja.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
    Long id,
    Long customerId,
    String customerName,
    Long productId,
    Integer rating,
    String comment,
    LocalDateTime createdAt
) {}
