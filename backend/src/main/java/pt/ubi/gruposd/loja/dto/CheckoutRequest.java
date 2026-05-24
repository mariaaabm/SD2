package pt.ubi.gruposd.loja.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

// Payload do endpoint de checkout que junta os itens do carrinho com os dados completos de morada de envio e o método de pagamento, com validações nos campos obrigatórios para evitar encomendas malformadas.
public record CheckoutRequest(
    @NotEmpty List<@Valid CartItemRequest> items,
    @NotBlank @Size(max = 150) String shippingName,
    @NotBlank @Size(max = 30)  String shippingPhone,
    @NotBlank @Size(max = 255) String shippingAddress,
    @Size(max = 255)           String shippingAddress2,
    @NotBlank @Size(max = 20)  String shippingPostalCode,
    @NotBlank @Size(max = 100) String shippingCity,
    @Size(max = 100)           String shippingRegion,
    @NotBlank @Size(max = 80)  String shippingCountry,
    @NotBlank @Size(max = 30)  String paymentMethod
) {
}
