package taskManager.dto;

import taskManager.entity.Prioridade;
import taskManager.entity.Status;

import java.time.LocalDateTime;

public record DtoResponse(
        Long id,
        String titulo,
        String descricao,
        Status status,
        Prioridade prioridade,
        LocalDateTime dataDeInicio,
        LocalDateTime dataDeFim,
        Boolean concluida
) {
}
