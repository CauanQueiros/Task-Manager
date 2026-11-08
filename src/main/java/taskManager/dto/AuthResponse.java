package taskManager.dto;

import java.time.Instant;

public record AuthResponse(
        String token,
        String tipo,
        Instant expiraEm,
        String nome,
        String email
) {
}
