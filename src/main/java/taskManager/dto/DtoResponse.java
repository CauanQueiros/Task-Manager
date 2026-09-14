package taskManager.dto;

import taskManager.entity.Status;

public record DtoResponse(
        String titulo,
        String descricao,
        Status status,
        Boolean concluida
) {
}
