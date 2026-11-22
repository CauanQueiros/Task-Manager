package taskManager.service;

import taskManager.entity.Prioridade;
import taskManager.entity.Status;
import taskManager.entity.Tarefa;
import taskManager.exception.IdNaoEncontradoException;
import taskManager.exception.TarefaNaoEncontradaException;
import taskManager.exception.TransicaoStatusInvalidaException;
import taskManager.repository.TarefaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TarefaService {

    private static final Sort ORDEM_PADRAO = Sort.by(Sort.Direction.ASC, "id");

    TarefaRepository tarefaRepository;

    public TarefaService(TarefaRepository tarefaRepository) {
        this.tarefaRepository = tarefaRepository;
    }

    public Tarefa criarTarefa (Tarefa novaTarefa){
        if (novaTarefa == null){
            throw new TarefaNaoEncontradaException("Tarefa não encontrada");
        }

        // Valores padrão quando o front não envia
        if (novaTarefa.getStatus() == null) {
            novaTarefa.setStatus(Status.FAZER);
        }
        if (novaTarefa.getPrioridade() == null) {
            novaTarefa.setPrioridade(Prioridade.MEDIA);
        }
        // Mantém "concluida" coerente com o status
        novaTarefa.setConcluida(novaTarefa.getStatus() == Status.CONCLUIDA);

        return tarefaRepository.save(novaTarefa);
    }

    /**
     * Lista as tarefas. Se a prioridade for informada, filtra direto no banco.
     * Lista vazia é um resultado válido (não é erro).
     */
    public List<Tarefa> listarTarefa (Prioridade prioridade){
        if (prioridade == null) {
            return tarefaRepository.findAll(ORDEM_PADRAO);
        }
        return tarefaRepository.findByPrioridade(prioridade, ORDEM_PADRAO);
    }

    /**
     * Move a tarefa no fluxo de status, respeitando as transições permitidas
     * (ver {@link Status#proximosPermitidos()}) e ajustando datas e "concluida":
     *  - ao entrar em EM_PROGRESSO: preenche dataDeInicio (se ainda vazia) e limpa dataDeFim
     *  - ao entrar em CONCLUIDA: preenche dataDeFim (e dataDeInicio, se estava vazia)
     */
    @Transactional
    public Tarefa alterarStatus(Long id, Status novoStatus) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new IdNaoEncontradoException("Id não encontrado: " + id));

        Status atual = tarefa.getStatus() != null ? tarefa.getStatus() : Status.FAZER;

        if (atual == novoStatus) {
            throw new TransicaoStatusInvalidaException("A tarefa já está com o status " + novoStatus);
        }
        if (!atual.podeIrPara(novoStatus)) {
            throw new TransicaoStatusInvalidaException(
                    "Transição não permitida: " + atual + " -> " + novoStatus
                            + ". Permitido a partir de " + atual + ": " + atual.proximosPermitidos());
        }

        LocalDateTime agora = LocalDateTime.now();
        switch (novoStatus) {
            case EM_PROGRESSO -> {
                if (tarefa.getDataDeInicio() == null) {
                    tarefa.setDataDeInicio(agora);
                }
                tarefa.setDataDeFim(null);
            }
            case CONCLUIDA -> {
                if (tarefa.getDataDeInicio() == null) {
                    tarefa.setDataDeInicio(agora);
                }
                tarefa.setDataDeFim(agora);
            }
            case FAZER -> tarefa.setDataDeFim(null);
        }

        tarefa.setStatus(novoStatus);
        tarefa.setConcluida(novoStatus == Status.CONCLUIDA);
        return tarefaRepository.save(tarefa);
    }

    public void deletarTarefa (Long id){
        if (!tarefaRepository.existsById(id)){
            throw new IdNaoEncontradoException("Id não encontrado: " + id);
        }
        tarefaRepository.deleteById(id);
    }
}
