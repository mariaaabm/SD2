package pt.ubi.gruposd.loja.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StatsRevenueResponse(
    LocalDate periodStart,
    LocalDate periodEnd,
    BigDecimal revenue
) {
}
