package taskManager.dto;

import taskManager.entity.Status;

public record DtoResponse(
        Long id,
        String titulo,
        String descricao,
        Status status,
        Boolean concluida
) {
}
