package pt.ubi.gruposd.loja.dto;

import java.util.List;
import org.springframework.data.domain.Page;

// Envelope genérico de paginação devolvido pela API que isola o frontend dos detalhes do Page do Spring Data, expõe apenas os campos relevantes e oferece um factory of() para converter facilmente de uma Page Spring.
public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {
    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }
}
