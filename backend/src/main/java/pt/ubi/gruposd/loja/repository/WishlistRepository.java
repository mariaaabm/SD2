package pt.ubi.gruposd.loja.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.ubi.gruposd.loja.model.WishlistItem;

// Repositório Spring Data JPA para wishlist com queries derivadas para listar por cliente, verificar se um par cliente-produto já existe e remover por esse mesmo par, evitando duplicados e suportando o toggle de favoritos.
public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    Optional<WishlistItem> findByCustomerIdAndProductId(Long customerId, Long productId);
    boolean existsByCustomerIdAndProductId(Long customerId, Long productId);
    void deleteByCustomerIdAndProductId(Long customerId, Long productId);
}
