package taskManager.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import taskManager.dto.CategoriaRequest;
import taskManager.dto.CategoriaResponse;
import taskManager.entity.Usuario;
import taskManager.mapper.CategoriaMapper;
import taskManager.service.CategoriaService;

import java.util.List;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<CategoriaResponse>> listar(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(CategoriaMapper.toResponseList(categoriaService.listar(usuario)));
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<CategoriaResponse> cadastrar(@RequestBody @Valid CategoriaRequest request, @AuthenticationPrincipal Usuario usuario) {
        CategoriaResponse response = CategoriaMapper.toDto(categoriaService.criar(request, usuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        categoriaService.deletar(id, usuario);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
