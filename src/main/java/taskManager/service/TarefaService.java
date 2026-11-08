package taskManager.service;

import taskManager.entity.Status;
import taskManager.entity.Tarefa;
import taskManager.entity.Usuario;
import taskManager.exception.IdNaoEncontradoException;
import taskManager.exception.TarefaNaoEncontradaException;
import taskManager.exception.TransicaoStatusInvalidaException;
import taskManager.repository.TarefaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TarefaService {

    TarefaRepository tarefaRepository;

    public TarefaService(TarefaRepository tarefaRepository) {
        this.tarefaRepository = tarefaRepository;
    }

    public Tarefa criarTarefa (Tarefa novaTarefa, Usuario usuario){
        if (novaTarefa != null){
            novaTarefa.setUsuario(usuario);
            return tarefaRepository.save(novaTarefa);
        }
        else {
            throw new TarefaNaoEncontradaException("Tarefa não encontrada");
        }
    }

    public Tarefa atualizarStatus(Long id, Status novoStatus, Usuario usuario) {
        Tarefa tarefa = tarefaRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new IdNaoEncontradoException("Id não encontrado: " + id));

        Status atual = tarefa.getStatus();
        if (atual != null && !atual.podeIrPara(novoStatus)) {
            throw new TransicaoStatusInvalidaException(
                    "Não é possível mudar de " + atual + " para " + novoStatus);
        }

        tarefa.setStatus(novoStatus);
        tarefa.setConcluida(novoStatus == Status.CONCLUIDA);
        if (novoStatus == Status.EM_PROGRESSO && tarefa.getDataDeInicio() == null) {
            tarefa.setDataDeInicio(LocalDateTime.now());
        }
        tarefa.setDataDeFim(novoStatus == Status.CONCLUIDA ? LocalDateTime.now() : null);

        return tarefaRepository.save(tarefa);
    }

    public List<Tarefa> listarTarefa (Usuario usuario){
        // lista vazia é um resultado válido (usuário novo ainda sem tarefas), não um erro 404
        return tarefaRepository.findByUsuarioId(usuario.getId());
    }

    public void deletarTarefa (Long id, Usuario usuario){
        // só encontra a tarefa se ela pertencer ao usuário logado; de outro usuário cai no mesmo 404
        Tarefa tarefa = tarefaRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new IdNaoEncontradoException("Id não encontrado: " + id));
        tarefaRepository.delete(tarefa);
    }
}
