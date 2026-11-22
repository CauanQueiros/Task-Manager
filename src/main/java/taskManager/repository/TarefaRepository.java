package taskManager.repository;

import taskManager.entity.Prioridade;
import taskManager.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.domain.Sort;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa,Long> {

    List<Tarefa> findByPrioridade(Prioridade prioridade, Sort sort);
}
