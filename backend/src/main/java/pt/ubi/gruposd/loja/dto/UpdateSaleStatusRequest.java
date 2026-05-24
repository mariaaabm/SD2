package pt.ubi.gruposd.loja.dto;

import jakarta.validation.constraints.NotNull;
import pt.ubi.gruposd.loja.model.SaleStatus;

// Payload do endpoint administrativo que muda o estado de uma encomenda, aceita um dos valores do enum SaleStatus garantindo que apenas estados válidos são submetidos.
public record UpdateSaleStatusRequest(
    @NotNull SaleStatus status
) {
}
