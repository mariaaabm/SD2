package pt.ubi.gruposd.loja.dto;

import java.util.List;

// Resumo de avaliação de um produto que junta a média de estrelas arredondada a uma casa decimal, a contagem total de reviews e a lista de reviews ordenada por data descendente.
public record ProductRatingResponse(
    double average,
    long count,
    List<ReviewResponse> reviews
) {}
