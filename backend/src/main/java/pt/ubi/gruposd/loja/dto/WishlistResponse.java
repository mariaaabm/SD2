package pt.ubi.gruposd.loja.dto;

import java.time.LocalDateTime;

// Vista de um item da wishlist com todos os dados do produto embutidos, evitando que o frontend tenha de juntar resultados de dois endpoints diferentes para mostrar a lista de favoritos.
public record WishlistResponse(
    Long id,
    Long productId,
    String productName,
    String productDescription,
    double price,
    Integer stock,
    Boolean active,
    String categoryName,
    String imageUrl,
    LocalDateTime addedAt
) {}
