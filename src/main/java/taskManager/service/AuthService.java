package taskManager.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import taskManager.dto.AuthResponse;
import taskManager.dto.LoginRequest;
import taskManager.dto.RegistroRequest;
import taskManager.entity.Role;
import taskManager.entity.Usuario;
import taskManager.exception.EmailJaCadastradoException;
import taskManager.repository.UsuarioRepository;

import java.time.Instant;
import java.util.Locale;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(AuthenticationManager authenticationManager,
                       UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public AuthResponse login(LoginRequest request) {
        Authentication autenticacao = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizarEmail(request.email()), request.senha()));
        return gerarResposta((Usuario) autenticacao.getPrincipal());
    }

    public AuthResponse registrar(RegistroRequest request) {
        String email = normalizarEmail(request.email());

        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailJaCadastradoException("Já existe uma conta com este e-mail");
        }

        Usuario novo = new Usuario(
                request.nome().trim(),
                email,
                passwordEncoder.encode(request.senha()),
                Role.USER);

        try {
            usuarioRepository.save(novo);
        } catch (DataIntegrityViolationException e) {
            // dois cadastros simultâneos com o mesmo e-mail: a constraint única do banco barrou o segundo
            throw new EmailJaCadastradoException("Já existe uma conta com este e-mail");
        }

        // já devolve o token para o usuário entrar direto após criar a conta
        return gerarResposta(novo);
    }

    private AuthResponse gerarResposta(Usuario usuario) {
        Instant expiraEm = tokenService.calcularExpiracao();
        String token = tokenService.gerarToken(usuario, expiraEm);
        return new AuthResponse(token, "Bearer", expiraEm, usuario.getNome(), usuario.getEmail());
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
