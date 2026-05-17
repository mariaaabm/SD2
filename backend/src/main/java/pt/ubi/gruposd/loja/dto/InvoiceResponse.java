package pt.ubi.gruposd.loja.dto;

import java.time.LocalDateTime;

public record InvoiceResponse(
    Long id,
    Long saleId,
    String invoiceNumber,
    LocalDateTime issuedAt
) {
}

