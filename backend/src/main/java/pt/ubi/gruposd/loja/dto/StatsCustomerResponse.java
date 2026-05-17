package pt.ubi.gruposd.loja.dto;

import java.math.BigDecimal;

public record StatsCustomerResponse(
    Long customerId,
    String customerName,
    Long totalPurchases,
    BigDecimal totalSpent
) {
}

