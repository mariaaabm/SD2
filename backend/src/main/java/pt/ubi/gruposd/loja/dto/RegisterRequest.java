package pt.ubi.gruposd.loja.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Payload do endpoint de registo com validações que exigem nome obrigatório, email válido até 180 caracteres e password com pelo menos 8 caracteres como medida mínima de segurança.
public record RegisterRequest(
    @NotBlank @Size(max = 150) String name,
    @Email @NotBlank @Size(max = 180) String email,
    @NotBlank @Size(min = 8, max = 100) String password
) {
}

