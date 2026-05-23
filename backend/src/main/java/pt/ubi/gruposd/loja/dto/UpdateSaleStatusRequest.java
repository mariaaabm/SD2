package pt.ubi.gruposd.loja.dto;

import jakarta.validation.constraints.NotNull;
import pt.ubi.gruposd.loja.model.SaleStatus;

public record UpdateSaleStatusRequest(
    @NotNull SaleStatus status
) {
}
