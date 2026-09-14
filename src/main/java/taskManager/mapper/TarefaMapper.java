package taskManager.mapper;

import taskManager.dto.DtoRequest;
import taskManager.dto.DtoResponse;
import taskManager.entity.Tarefa;

import java.util.List;
import java.util.stream.Collectors;

public class TarefaMapper {

    public static Tarefa toEntity (DtoRequest dto){
        return new Tarefa(
                dto.titulo(),
                dto.descricao(),
                dto.status(),
                dto.dataDeInicio(),
                dto.dataDeFim(),
                dto.concluida()
        );
    }

    public static DtoResponse toDto (Tarefa tarefa){
        return new DtoResponse(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.getStatus(),
                tarefa.getConcluida()
        );
    }

    public static List<DtoResponse> toResponseList(List<Tarefa> tarefas) {
        return tarefas.stream()
                .map(TarefaMapper::toDto)
                .collect(Collectors.toList());
    }
}
