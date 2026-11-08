package taskManager.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import taskManager.entity.Usuario;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class TokenService {

    private static final String ISSUER = "task-manager-api";

    private final String secret;
    private final long expiracaoHoras;

    public TokenService(
            @Value("${api.security.token.secret}") String secret,
            @Value("${api.security.token.expiracao-horas:2}") long expiracaoHoras) {
        this.secret = secret;
        this.expiracaoHoras = expiracaoHoras;
    }

    public String gerarToken(Usuario usuario, Instant expiraEm) {
        try {
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getEmail())
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(expiraEm)
                    .sign(Algorithm.HMAC256(secret));
        } catch (JWTCreationException e) {
            throw new IllegalStateException("Erro ao gerar o token de acesso", e);
        }
    }

    /**
     * Retorna o e-mail (subject) do token ou vazio se o token for inválido/expirado.
     */
    public Optional<String> validarToken(String token) {
        try {
            String email = JWT.require(Algorithm.HMAC256(secret))
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
            return Optional.ofNullable(email);
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }

    public Instant calcularExpiracao() {
        return Instant.now().plus(expiracaoHoras, ChronoUnit.HOURS);
    }
}
