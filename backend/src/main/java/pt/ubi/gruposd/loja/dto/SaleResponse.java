package pt.ubi.gruposd.loja.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import pt.ubi.gruposd.loja.model.SaleStatus;

// Vista de uma venda devolvida pela API com os totais já discriminados em base tributável, IVA e total, lista de itens, fatura associada e dados completos de envio e pagamento para o frontend mostrar a encomenda sem pedidos adicionais.
public record SaleResponse(
    Long id,
    Long customerId,
    String customerName,
    LocalDateTime createdAt,
    BigDecimal subtotal,
    BigDecimal vatAmount,
    BigDecimal vatRate,
    BigDecimal total,
    SaleStatus status,
    List<SaleItemResponse> items,
    InvoiceResponse invoice,
    String shippingName,
    String shippingPhone,
    String shippingAddress,
    String shippingAddress2,
    String shippingPostalCode,
    String shippingCity,
    String shippingRegion,
    String shippingCountry,
    String paymentMethod
) {
}
