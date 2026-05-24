package pt.ubi.gruposd.loja.dto;

import jakarta.validation.constraints.Size;

// Payload para atualização do perfil do cliente autenticado com todos os campos opcionais, e o service só altera os que vierem preenchidos, exigindo a password atual antes de aceitar uma nova.
public record UpdateProfileRequest(
    @Size(max = 150) String name,
    String currentPassword,
    @Size(min = 8, max = 100) String newPassword
) {}
