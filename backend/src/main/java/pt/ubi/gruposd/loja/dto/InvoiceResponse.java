package pt.ubi.gruposd.loja.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record InvoiceResponse(
    Long id,
    Long saleId,
    String documentType,
    String series,
    String invoiceNumber,
    String formattedNumber,
    LocalDateTime issuedAt,
    LocalDateTime operationDate,
    InvoiceIssuer issuer,
    InvoiceParty customer,
    InvoiceParty shippingTo,
    List<InvoiceLine> lines,
    List<InvoiceVatSummary> vatSummary,
    BigDecimal subtotal,
    BigDecimal vatTotal,
    BigDecimal total,
    String currency,
    String paymentMethod,
    String paymentTerms,
    String notes,
    String atcud,
    String hashControl,
    String certificationText
) {
}
