package taskManager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank(message = "Informe o nome")
        @Size(max = 100, message = "O nome pode ter no máximo 100 caracteres")
        String nome,
        @NotBlank(message = "Informe o e-mail")
        @Email(message = "E-mail inválido")
        @Size(max = 150, message = "O e-mail pode ter no máximo 150 caracteres")
        String email,
        @NotBlank(message = "Informe a senha")
        @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres")
        String senha
) {
}
