package pt.ubi.gruposd.loja.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pt.ubi.gruposd.loja.model.Review;

// Repositório Spring Data JPA para reviews com queries para listar por produto, encontrar e verificar a review existente do par cliente-produto, e queries JPQL agregadas para calcular a média e a contagem de avaliações que aparecem no resumo do produto.
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);
    Optional<Review> findByCustomerIdAndProductId(Long customerId, Long productId);
    boolean existsByCustomerIdAndProductId(Long customerId, Long productId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double avgRatingByProductId(Long productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    long countByProductId(Long productId);
}
