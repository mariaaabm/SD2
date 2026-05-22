package pt.ubi.gruposd.loja.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
    @Size(max = 150) String name,
    String currentPassword,
    @Size(min = 8, max = 100) String newPassword
) {}
