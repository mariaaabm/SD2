package pt.ubi.gruposd.loja.dto;

import java.math.BigDecimal;

public record InvoiceLine(
    Long productId,
    String description,
    Integer quantity,
    BigDecimal unitPriceNet,
    BigDecimal unitPriceGross,
    BigDecimal vatRate,
    BigDecimal lineNet,
    BigDecimal vatAmount,
    BigDecimal lineGross
) {
}
