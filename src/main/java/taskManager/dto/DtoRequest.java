package taskManager.dto;

import taskManager.entity.Prioridade;
import taskManager.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record DtoRequest(
        @NotBlank
        @Size(max = 255, message = "O título deve ter no máximo 255 caracteres")
        String titulo,
        @NotBlank
        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
        String descricao,
        Status status,
        Prioridade prioridade,
        LocalDateTime dataDeInicio,
        LocalDateTime dataDeFim,
        @NotNull
        Boolean concluida
) {
}
