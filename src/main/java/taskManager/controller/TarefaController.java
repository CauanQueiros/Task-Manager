package taskManager.controller;

import taskManager.dto.DtoResponse;
import taskManager.dto.DtoRequest;
import taskManager.dto.DtoStatusRequest;
import taskManager.entity.Prioridade;
import taskManager.entity.Tarefa;
import taskManager.mapper.TarefaMapper;
import taskManager.service.TarefaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/tarefa")
public class TarefaController {

    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<DtoResponse>> listarTasks(@RequestParam(required = false) Prioridade prioridade) {
        List<Tarefa> tarefa = tarefaService.listarTarefa(prioridade);
        List<DtoResponse> response = TarefaMapper.toResponseList(tarefa);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<DtoResponse> cadastrarTask(@RequestBody @Valid DtoRequest dtoRequest) {
        Tarefa tarefa = TarefaMapper.toEntity(dtoRequest);
        Tarefa cadastrarTask = tarefaService.criarTarefa(tarefa);
        DtoResponse response = TarefaMapper.toDto(cadastrarTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DtoResponse> alterarStatus(@PathVariable Long id,
                                                     @RequestBody @Valid DtoStatusRequest dtoStatus) {
        Tarefa atualizada = tarefaService.alterarStatus(id, dtoStatus.status());
        return ResponseEntity.status(HttpStatus.OK).body(TarefaMapper.toDto(atualizada));
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarTask(@PathVariable Long id) {
        tarefaService.deletarTarefa(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
