package pt.ubi.gruposd.loja.dto;

import java.math.BigDecimal;

// Linha do resumo de IVA da fatura que agrupa por taxa, mostrando a base tributável e o valor de IVA correspondente, como exigido na apresentação visual de faturas em Portugal.
public record InvoiceVatSummary(
    BigDecimal rate,
    BigDecimal base,
    BigDecimal amount
) {
}
