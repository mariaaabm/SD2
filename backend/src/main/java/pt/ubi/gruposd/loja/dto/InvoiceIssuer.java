package pt.ubi.gruposd.loja.dto;

public record InvoiceIssuer(
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
    public static InvoiceIssuer sportFlowDefault() {
        return new InvoiceIssuer(
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
