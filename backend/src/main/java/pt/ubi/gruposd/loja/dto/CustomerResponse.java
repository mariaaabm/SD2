package pt.ubi.gruposd.loja.dto;

import pt.ubi.gruposd.loja.model.UserRole;

// Vista pública de um cliente devolvida pela API sem expor o hash da password nem outros campos sensíveis da entidade.
public record CustomerResponse(
    Long id,
    String name,
    String email,
    UserRole role
) {
}

