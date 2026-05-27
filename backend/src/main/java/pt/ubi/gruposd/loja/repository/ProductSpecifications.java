package pt.ubi.gruposd.loja.repository;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import pt.ubi.gruposd.loja.model.Product;

// Constrói as Specifications JPA que aplicam os filtros dinâmicos da pesquisa de produtos por categoria, por flag de ativo e por termo livre via LIKE, evitando ter de escrever várias variantes do mesmo query no repositório.
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
                // A colacao utf8mb4_0900_ai_ci (configurada na migração Flyway V10) torna o LIKE
                // insensível a maiúsculas/minúsculas e a acentos na base de dados MySQL/MariaDB.
                // Em H2 (testes) este comportamento pode ser diferente — o fallback fuzzy cobre esses casos.
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
