package pt.ubi.gruposd.loja.repository;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import pt.ubi.gruposd.loja.model.Product;

// Constrói filtros dinâmicos JPA para a pesquisa de produtos.
// Combina filtros por categoria, por estado (ativo/inativo) e por texto via LIKE numa única query.
public class ProductSpecifications {
    private ProductSpecifications() {}

    public static Specification<Product> withFilters(Long categoryId, Boolean activeOnly, String search) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            if (Boolean.TRUE.equals(activeOnly)) {
                predicates.add(cb.isTrue(root.get("active")));
            }

            if (search != null && !search.isBlank()) {
                // O LIKE é case-insensitive e ignora acentos em MySQL (colação utf8mb4_0900_ai_ci).
                // Em H2 (testes) o comportamento pode diferir — o fallback fuzzy cobre esses casos.
                String pattern = "%" + search.trim() + "%";
                predicates.add(cb.or(
                    cb.like(root.get("name"), pattern),
                    cb.like(root.get("description"), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
