package pt.ubi.gruposd.loja.dto;

import java.math.BigDecimal;

// Vista de uma linha da venda com o preço unitário com IVA, o preço unitário sem IVA, a taxa aplicada, o valor de IVA e os subtotais já calculados para a fatura conseguir mostrar tudo discriminado.
public record SaleItemResponse(
    Long id,
    Long productId,
    String productName,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal unitPriceNet,
    BigDecimal vatRate,
    BigDecimal vatAmount,
    BigDecimal subtotalNet,
    BigDecimal subtotal
) {
}
