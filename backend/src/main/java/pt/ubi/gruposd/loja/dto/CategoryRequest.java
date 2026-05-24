package pt.ubi.gruposd.loja.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Payload para criação e atualização de categoria com nome obrigatório até 120 caracteres e descrição opcional até 1000 caracteres para o resumo mostrado no frontend.
public record CategoryRequest(
    @NotBlank @Size(max = 120) String name,
    @Size(max = 1000) String description
) {
}

