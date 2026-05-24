package pt.ubi.gruposd.loja.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pt.ubi.gruposd.loja.model.Product;

// Repositório Spring Data JPA para produtos que estende JpaSpecificationExecutor para suportar pesquisa com filtros dinâmicos via ProductSpecifications, e disponibiliza existsByCategoryId para o CategoryService bloquear a remoção de categorias com produtos.
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    boolean existsByCategoryId(Long categoryId);
}
