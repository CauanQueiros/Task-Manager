package taskManager.dto;

import jakarta.validation.constraints.NotNull;
import taskManager.entity.Status;

public record DtoStatusRequest(
        @NotNull(message = "O novo status é obrigatório")
        Status status
) {
}
