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

// Gere os refresh tokens que permitem prolongar a sessão sem voltar a pedir credenciais, gera tokens aleatórios via UUID e implementa rotação ao validar para que cada refresh consuma o token antigo e crie um novo, mitigando ataques por reutilização.
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

    // Cria um refresh token UUID aleatório com validade configurável (padrão 7 dias).
    // UUID.randomUUID() usa SecureRandom internamente, o que é suficientemente seguro para este uso.
    @Transactional
    public String createForCustomer(Customer customer) {
        RefreshToken rt = new RefreshToken();
        rt.setCustomer(customer);
        rt.setToken(UUID.randomUUID().toString());
        rt.setExpiresAt(Instant.now().plus(refreshDays, ChronoUnit.DAYS));
        return repo.save(rt).getToken();
    }

    // Valida o refresh token, verifica que ainda não expirou, apaga-o e emite imediatamente um novo, devolvendo também o cliente associado para o caller poder gerar um JWT de acesso fresco.
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
