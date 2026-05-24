package pt.ubi.gruposd.loja.dto;

// Resposta dos endpoints de login e registo que devolve o JWT de acesso e os dados básicos do cliente autenticado para o frontend popular logo o estado de sessão.
public record AuthResponse(
    String token,
    CustomerResponse customer
) {
}

