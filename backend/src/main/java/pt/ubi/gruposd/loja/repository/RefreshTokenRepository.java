package pt.ubi.gruposd.loja.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pt.ubi.gruposd.loja.model.RefreshToken;

// Repositório Spring Data JPA para refresh tokens com lookup pelo valor do token usado na rotação e remoção por token usada no logout.
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
}
