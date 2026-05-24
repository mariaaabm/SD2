package pt.ubi.gruposd.loja.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Payload para criar ou atualizar a review de um produto com rating obrigatório entre 1 e 5 estrelas e comentário opcional até 1000 caracteres.
public record ReviewRequest(
    @NotNull @Min(1) @Max(5) Integer rating,
    @Size(max = 1000) String comment
) {}
