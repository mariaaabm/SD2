package pt.ubi.gruposd.loja.dto;

public record StatsProductResponse(
    Long productId,
    String productName,
    Long quantitySold
) {
}

