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

// Gera e valida JSON Web Tokens (JWT) assinados com HMAC-SHA256.
// Cada token inclui o email, id e role do utilizador para não precisar de ir à base de dados a cada pedido.
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

    // Cria o JWT com o email (subject), id e role do utilizador, assinado com a chave secreta.
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

    // Extrai o email (subject) do token. Usar sempre antes de isTokenValid para verificar a assinatura.
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Verifica se o token pertence ao utilizador e ainda não expirou.
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Lê os claims do JWT verificando a assinatura. Lança exceção se o token for inválido ou expirado.
    private Claims extractClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
