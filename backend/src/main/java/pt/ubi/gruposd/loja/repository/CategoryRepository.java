package pt.ubi.gruposd.loja.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ubi.gruposd.loja.model.Category;

// Repositório Spring Data JPA para categorias com o método derivado existsByNameIgnoreCase usado para garantir a unicidade do nome ao criar ou renomear, ignorando diferenças de maiúsculas.
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCase(String name);
}
