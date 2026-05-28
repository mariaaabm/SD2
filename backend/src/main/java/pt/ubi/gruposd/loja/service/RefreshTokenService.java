package pt.ubi.gruposd.loja.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ubi.gruposd.loja.exception.UnauthorizedException;
import pt.ubi.gruposd.loja.model.Customer;
import pt.ubi.gruposd.loja.model.RefreshToken;
import pt.ubi.gruposd.loja.repository.RefreshTokenRepository;

// Gere os refresh tokens que prolongam a sessão sem pedir login novamente.
// Usa rotação: ao renovar, o token antigo é eliminado e um novo é criado (evita reutilização de tokens roubados).
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository repo;
    private final long refreshDays;

    public RefreshTokenService(
        RefreshTokenRepository repo,
        @Value("${app.security.refresh-expiration-days:7}") long refreshDays
    ) {
        this.repo = repo;
        this.refreshDays = refreshDays;
    }

    // Cria um token UUID aleatório (usa SecureRandom internamente) com validade de 7 dias por defeito.
    @Transactional
    public String createForCustomer(Customer customer) {
        RefreshToken rt = new RefreshToken();
        rt.setCustomer(customer);
        rt.setToken(UUID.randomUUID().toString());
        rt.setExpiresAt(Instant.now().plus(refreshDays, ChronoUnit.DAYS));
        return repo.save(rt).getToken();
    }

    // Verifica se o token é válido e não expirou, apaga-o e cria um novo. Devolve o cliente para gerar um JWT novo.
    @Transactional
    public RefreshResult validateAndRotate(String token) {
        RefreshToken rt = repo.findByToken(token)
            .filter(t -> t.getExpiresAt().isAfter(Instant.now()))
            .orElseThrow(() -> new UnauthorizedException("Sessao expirada. Faz login novamente."));

        Customer customer = rt.getCustomer();
        repo.delete(rt);

        RefreshToken newRt = new RefreshToken();
        newRt.setCustomer(customer);
        newRt.setToken(UUID.randomUUID().toString());
        newRt.setExpiresAt(Instant.now().plus(refreshDays, ChronoUnit.DAYS));
        String newToken = repo.save(newRt).getToken();

        return new RefreshResult(customer, newToken);
    }

    @Transactional
    public void deleteByToken(String token) {
        repo.deleteByToken(token);
    }

    public record RefreshResult(Customer customer, String newRefreshToken) {}
}
