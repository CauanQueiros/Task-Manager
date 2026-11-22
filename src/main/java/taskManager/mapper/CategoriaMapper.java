package taskManager.mapper;

import taskManager.dto.CategoriaResponse;
import taskManager.entity.Categoria;

import java.util.List;

public class CategoriaMapper {

    public static CategoriaResponse toDto(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        return new CategoriaResponse(categoria.getId(), categoria.getNome(), categoria.getCor());
    }

    public static List<CategoriaResponse> toResponseList(List<Categoria> categorias) {
        return categorias.stream().map(CategoriaMapper::toDto).toList();
    }
}
