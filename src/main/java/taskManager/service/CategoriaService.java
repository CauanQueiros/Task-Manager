package taskManager.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import taskManager.dto.CategoriaRequest;
import taskManager.entity.Categoria;
import taskManager.entity.Usuario;
import taskManager.exception.CategoriaJaExisteException;
import taskManager.exception.CategoriaNaoEncontradaException;
import taskManager.repository.CategoriaRepository;
import taskManager.repository.TarefaRepository;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final TarefaRepository tarefaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository, TarefaRepository tarefaRepository) {
        this.categoriaRepository = categoriaRepository;
        this.tarefaRepository = tarefaRepository;
    }

    public List<Categoria> listar(Usuario usuario) {
        return categoriaRepository.findByUsuarioIdOrderByNomeAsc(usuario.getId());
    }

    public Categoria criar(CategoriaRequest request, Usuario usuario) {
        String nome = request.nome().trim();
        if (categoriaRepository.existsByUsuarioIdAndNomeIgnoreCase(usuario.getId(), nome)) {
            throw new CategoriaJaExisteException("Você já tem uma categoria chamada \"" + nome + "\"");
        }

        String cor = request.cor() == null || request.cor().isBlank()
                ? Categoria.COR_PADRAO
                : request.cor().toLowerCase();

        try {
            return categoriaRepository.save(new Categoria(nome, cor, usuario));
        } catch (DataIntegrityViolationException e) {
            // duas requisições simultâneas com o mesmo nome: a constraint única do banco barrou a segunda
            throw new CategoriaJaExisteException("Você já tem uma categoria chamada \"" + nome + "\"");
        }
    }

    /**
     * Só encontra a categoria se ela for do usuário logado; de outro usuário cai no mesmo 404.
     */
    public Categoria buscarDoUsuario(Long id, Usuario usuario) {
        return categoriaRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new CategoriaNaoEncontradaException("Categoria não encontrada: " + id));
    }

    /**
     * Excluir uma categoria não apaga as tarefas dela: elas apenas ficam sem categoria.
     */
    @Transactional
    public void deletar(Long id, Usuario usuario) {
        Categoria categoria = buscarDoUsuario(id, usuario);
        tarefaRepository.desvincularCategoria(categoria.getId(), usuario.getId());
        categoriaRepository.delete(categoria);
    }
}
