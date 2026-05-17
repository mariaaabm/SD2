package pt.ubi.gruposd.loja.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ubi.gruposd.loja.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCase(String name);
}
