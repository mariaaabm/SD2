package pt.ubi.gruposd.loja.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleResponse(
    Long id,
    Long customerId,
    String customerName,
    LocalDateTime createdAt,
    BigDecimal total,
    String status,
    List<SaleItemResponse> items,
    InvoiceResponse invoice
) {
}

