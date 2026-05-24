package pt.ubi.gruposd.loja.dto;

import java.math.BigDecimal;

// Vista de um produto devolvida pela API com todos os campos visíveis ao cliente, incluindo o nome da categoria já resolvido para evitar lazy loading desnecessário no frontend.
public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    Boolean active,
    Long categoryId,
    String categoryName,
    String imageUrl
) {
}

