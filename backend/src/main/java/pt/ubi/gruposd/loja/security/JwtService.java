package pt.ubi.gruposd.loja.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pt.ubi.gruposd.loja.model.Customer;

// Gera e valida os JWT que autenticam pedidos à API, assina-os com uma chave HMAC-SHA256 derivada do segredo configurado em application.yml e inclui claims com o id do cliente e o role para o servidor não ter de consultar a base de dados em cada pedido.
@Service
public class JwtService {
    private final SecretKey secretKey;
    private final long expirationMinutes;

    public JwtService(
        @Value("${app.security.jwt-secret}") String jwtSecret,
        @Value("${app.security.jwt-expiration-minutes}") long expirationMinutes
    ) {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    // Gera um JWT assinado com HMAC-SHA256 contendo o email como subject e customerId e role como claims extras.
    // O role no claim evita uma consulta extra à base de dados em cada pedido para verificar permissões.
    public String generateToken(Customer customer) {
        Instant now = Instant.now();

        return Jwts.builder()
            .subject(customer.getEmail())
            .claim("customerId", customer.getId())
            .claim("role", customer.getRole().name())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(expirationMinutes * 60)))
            .signWith(secretKey)
            .compact();
    }

    // Extrai o subject (email do utilizador) do JWT sem verificar a assinatura — deve ser seguido de isTokenValid.
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Verifica que o token pertence ao utilizador carregado e que ainda não expirou.
    // Ambas as condições são necessárias para aceitar o pedido como autenticado.
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Verifica a assinatura e devolve os claims do JWT; lança exceção se a assinatura for inválida ou o token malformado.
    private Claims extractClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
