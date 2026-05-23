package pt.ubi.gruposd.loja.dto;

import java.util.List;

public record ProductRatingResponse(
    double average,
    long count,
    List<ReviewResponse> reviews
) {}
