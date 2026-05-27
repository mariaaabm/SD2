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
    InvoiceResponse.Issuer issuer,
    InvoiceResponse.Party customer,
    InvoiceResponse.Party shippingTo,
    List<InvoiceResponse.Line> lines,
    List<InvoiceResponse.VatSummary> vatSummary,
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
    public record Issuer(
        String companyName,
        String taxId,
        String address,
        String postalCode,
        String city,
        String country,
        String email,
        String phone,
        String website,
        String shareCapital,
        String registryInfo
    ) {
        public static Issuer sportFlowDefault() {
            return new Issuer(
                "SportFlow, Lda.",
                "PT500123456",
                "Rua Marques d'Avila e Bolama, 6201-001",
                "6201-001",
                "Covilha",
                "Portugal",
                "faturacao@sportflow.pt",
                "+351 275 000 000",
                "www.sportflow.pt",
                "5 000,00 EUR",
                "C.R.C. Castelo Branco"
            );
        }
    }

    public record Party(
        String name,
        String taxId,
        String email,
        String phone,
        String address,
        String address2,
        String postalCode,
        String city,
        String region,
        String country
    ) {}

    public record Line(
        Long productId,
        String description,
        Integer quantity,
        BigDecimal unitPriceNet,
        BigDecimal unitPriceGross,
        BigDecimal vatRate,
        BigDecimal lineNet,
        BigDecimal vatAmount,
        BigDecimal lineGross
    ) {}

    public record VatSummary(
        BigDecimal rate,
        BigDecimal base,
        BigDecimal amount
    ) {}
}
