package pt.ubi.gruposd.loja.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

// Payload para criação e atualização de produto com validações que garantem nome obrigatório, preço não negativo, stock não negativo e categoria existente, com active opcional para suportar criar produtos já desativados.
public record ProductRequest(
    @NotBlank @Size(max = 150) String name,
    @Size(max = 1500) String description,
    @NotNull @DecimalMin("0.00") BigDecimal price,
    @NotNull @Min(0) Integer stock,
    @NotNull Long categoryId,
    Boolean active,
    @Size(max = 500) String imageUrl
) {
}

