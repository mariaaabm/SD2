package pt.ubi.gruposd.loja.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Payload do endpoint de login com validações que exigem email num formato válido e password não vazia, evitando que pedidos malformados cheguem ao service.
public record LoginRequest(
    @Email @NotBlank String email,
    @NotBlank String password
) {
}

