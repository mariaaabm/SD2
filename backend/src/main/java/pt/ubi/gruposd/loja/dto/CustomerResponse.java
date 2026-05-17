package pt.ubi.gruposd.loja.dto;

import pt.ubi.gruposd.loja.model.UserRole;

public record CustomerResponse(
    Long id,
    String name,
    String email,
    UserRole role
) {
}

