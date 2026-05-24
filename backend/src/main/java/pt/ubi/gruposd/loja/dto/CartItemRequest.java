package pt.ubi.gruposd.loja.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Representa uma linha do carrinho enviada no checkout, com productId obrigatório e quantity validada a ser pelo menos 1 para rejeitar pedidos com quantidades inválidas logo na camada HTTP.
public record CartItemRequest(
    @NotNull Long productId,
    @NotNull @Min(1) Integer quantity
) {
}

