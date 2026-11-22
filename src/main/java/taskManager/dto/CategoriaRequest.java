package taskManager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @NotBlank(message = "Informe o nome da categoria")
        @Size(max = 60, message = "O nome da categoria pode ter no máximo 60 caracteres")
        String nome,
        @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "Cor inválida: use o formato #RRGGBB")
        String cor
) {
}
