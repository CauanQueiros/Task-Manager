package taskManager.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import taskManager.entity.Prioridade;
import taskManager.entity.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa,Long> {

    // o EntityGraph já traz a categoria junto, evitando uma consulta extra por tarefa (N+1)
    @EntityGraph(attributePaths = "categoria")
    List<Tarefa> findByUsuarioId(Long usuarioId);

    @EntityGraph(attributePaths = "categoria")
    List<Tarefa> findByUsuarioIdAndPrioridade(Long usuarioId, Prioridade prioridade);

    @EntityGraph(attributePaths = "categoria")
    List<Tarefa> findByUsuarioIdAndCategoriaId(Long usuarioId, Long categoriaId);

    @EntityGraph(attributePaths = "categoria")
    List<Tarefa> findByUsuarioIdAndPrioridadeAndCategoriaId(Long usuarioId, Prioridade prioridade, Long categoriaId);

    @EntityGraph(attributePaths = "categoria")
    Optional<Tarefa> findByIdAndUsuarioId(Long id, Long usuarioId);

    /** Usado ao excluir uma categoria: as tarefas dela continuam existindo, só ficam sem categoria. */
    @Modifying
    @Query("update Tarefa t set t.categoria = null where t.categoria.id = :categoriaId and t.usuario.id = :usuarioId")
    int desvincularCategoria(@Param("categoriaId") Long categoriaId, @Param("usuarioId") Long usuarioId);
}
