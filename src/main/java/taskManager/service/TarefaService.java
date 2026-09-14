package taskManager.service;

import taskManager.entity.Status;
import taskManager.entity.Tarefa;
import taskManager.exception.IdNaoEncontradoException;
import taskManager.exception.TarefaNaoEncontradaException;
import taskManager.repository.TarefaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarefaService {

    TarefaRepository tarefaRepository;

    public TarefaService(TarefaRepository tarefaRepository) {
        this.tarefaRepository = tarefaRepository;
    }

    public Tarefa criarTarefa (Tarefa novaTarefa){
        if (novaTarefa != null){
            return tarefaRepository.save(novaTarefa);
        }
        else {
            throw new TarefaNaoEncontradaException("Tarefa não encontrada");
        }
    }

    public List<Tarefa> listarTarefa (){
        if (tarefaRepository.findAll().isEmpty()){
            throw new TarefaNaoEncontradaException("Tarefa não encontrada");
        }
        return tarefaRepository.findAll();
    }

    public void deletarTarefa (Long id){
        if (!tarefaRepository.existsById(id)){
            throw new IdNaoEncontradoException("Id não encontrado: " + id);
        }
        tarefaRepository.deleteById(id);
    }
}
