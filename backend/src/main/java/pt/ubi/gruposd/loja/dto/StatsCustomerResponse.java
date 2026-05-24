package pt.ubi.gruposd.loja.dto;

import java.math.BigDecimal;

// Linha do ranking de melhores clientes calculado por valor total gasto e número de encomendas, usado no dashboard de administração para identificar os clientes mais ativos.
public record StatsCustomerResponse(
    Long customerId,
    String customerName,
    Long totalPurchases,
    BigDecimal totalSpent
) {
}

