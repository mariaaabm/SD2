package pt.ubi.gruposd.loja.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequest(
    @NotBlank @Size(max = 150) String name,
    @Size(max = 1500) String description,
    @NotNull @DecimalMin("0.00") BigDecimal price,
    @NotNull @Min(0) Integer stock,
    @NotNull Long categoryId,
    Boolean active
) {
}

