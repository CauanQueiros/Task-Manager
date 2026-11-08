package taskManager.controller;

import taskManager.dto.DtoResponse;
import taskManager.dto.DtoRequest;
import taskManager.entity.Status;
import taskManager.entity.Tarefa;
import taskManager.entity.Usuario;
import taskManager.mapper.TarefaMapper;
import taskManager.service.TarefaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<List<DtoResponse>> listarTasks(@AuthenticationPrincipal Usuario usuario) {
        List<Tarefa> tarefa = tarefaService.listarTarefa(usuario);
        List<DtoResponse> response = TarefaMapper.toResponseList(tarefa);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<DtoResponse> cadastrarTask(@RequestBody @Valid DtoRequest dtoRequest,
                                                     @AuthenticationPrincipal Usuario usuario) {
        Tarefa tarefa = TarefaMapper.toEntity(dtoRequest);
        Tarefa cadastrarTask = tarefaService.criarTarefa(tarefa, usuario);
        DtoResponse response = TarefaMapper.toDto(cadastrarTask);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarTask(@PathVariable Long id,
                                            @AuthenticationPrincipal Usuario usuario) {
        tarefaService.deletarTarefa(id, usuario);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
