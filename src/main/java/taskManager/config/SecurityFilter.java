package taskManager.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import taskManager.repository.UsuarioRepository;
import taskManager.service.TokenService;

import java.io.IOException;

/**
 * Lê o header "Authorization: Bearer <token>", valida o JWT e autentica o usuário na requisição.
 * Se o token não existir ou for inválido, a requisição segue sem autenticação e o
 * Spring Security responde 401 nas rotas protegidas.
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final String PREFIXO = "Bearer ";

    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    public SecurityFilter(TokenService tokenService, UsuarioRepository usuarioRepository) {
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = recuperarToken(request);

        if (token != null) {
            tokenService.validarToken(token)
                    .flatMap(usuarioRepository::findByEmail)
                    .ifPresent(usuario -> {
                        var autenticacao = new UsernamePasswordAuthenticationToken(
                                usuario, null, usuario.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(autenticacao);
                    });
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(PREFIXO)) {
            return null;
        }
        String token = header.substring(PREFIXO.length()).trim();
        return token.isEmpty() ? null : token;
    }
}
