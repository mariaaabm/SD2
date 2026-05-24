package pt.ubi.gruposd.loja.dto;

// Vista de uma categoria devolvida pela API com id, nome e descrição, usada tanto no menu de navegação como na área de administração.
public record CategoryResponse(
    Long id,
    String name,
    String description
) {
}

