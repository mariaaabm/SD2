package pt.ubi.gruposd.loja.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

// Resposta do endpoint de receita agregada que devolve o intervalo de datas considerado e a soma de vendas nesse intervalo, permitindo ao frontend mostrar tanto o valor como o período exato apresentado.
public record StatsRevenueResponse(
    LocalDate periodStart,
    LocalDate periodEnd,
    BigDecimal revenue
) {
}
