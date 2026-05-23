package pt.ubi.gruposd.loja.dto;

import java.time.LocalDateTime;

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
