package pt.ubi.gruposd.loja.dto;

import java.math.BigDecimal;

public record InvoiceVatSummary(
    BigDecimal rate,
    BigDecimal base,
    BigDecimal amount
) {
}
