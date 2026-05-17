package pt.ubi.gruposd.loja.dto;

public record AuthResponse(
    String token,
    CustomerResponse customer
) {
}

