package pt.ubi.gruposd.loja.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.ubi.gruposd.loja.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByActiveTrue();
    List<Product> findByCategoryIdAndActiveTrue(Long categoryId);
    boolean existsByCategoryId(Long categoryId);
}
