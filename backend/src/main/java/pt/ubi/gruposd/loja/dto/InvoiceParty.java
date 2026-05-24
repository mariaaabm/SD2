package pt.ubi.gruposd.loja.dto;

// Dados de uma entidade envolvida na fatura, usada tanto para o adquirente como para o destinatário da entrega quando este é diferente do cliente, com taxId nullable porque o adquirente pode ser Consumidor Final.
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
