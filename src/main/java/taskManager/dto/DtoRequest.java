package taskManager.dto;

import taskManager.entity.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record DtoRequest(
        @NotBlank
        String titulo,
        @NotBlank
        String descricao,
        @Enumerated(value = EnumType.STRING)
        Status status,
        LocalDateTime dataDeInicio,
        LocalDateTime dataDeFim,
        @NotNull
        Boolean concluida
) {
}
