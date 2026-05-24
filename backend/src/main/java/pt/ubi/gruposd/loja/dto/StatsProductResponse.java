package pt.ubi.gruposd.loja.dto;

// Linha do ranking de produtos por unidades vendidas usado no dashboard de administração, com nome do produto já resolvido para evitar consultas extras no frontend.
public record StatsProductResponse(
    Long productId,
    String productName,
    Long quantitySold
) {
}

