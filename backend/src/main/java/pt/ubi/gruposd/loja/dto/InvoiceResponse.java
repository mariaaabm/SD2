package pt.ubi.gruposd.loja.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Vista completa da fatura no formato PT que junta emitente, adquirente, morada de entrega, linhas detalhadas com IVA discriminado, resumo de IVA por taxa, totais, método de pagamento e metadados de certificação como ATCUD e hash de controlo.
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
