package pt.ubi.gruposd.loja.dto;

public record InvoiceParty(
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
) {
}
