package pt.ubi.gruposd.loja.dto;

import java.math.BigDecimal;

// Linha detalhada da fatura com descrição do produto, quantidade, preço unitário sem e com IVA, taxa aplicada, valor de IVA e totais da linha, com todos os campos pré-calculados para o frontend só ter de formatar e mostrar.
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
