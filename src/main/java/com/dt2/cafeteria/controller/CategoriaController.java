package com.dt2.cafeteria.controller;

import com.dt2.cafeteria.entities.Categoria;
import com.dt2.cafeteria.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Categoria> categoria = categoriaRepository.findById(id);
        if (categoria.isEmpty()) {
            return ResponseEntity.status(404).body("Categoria não encontrada.");
        }
        return ResponseEntity.ok(categoria.get());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Categoria categoria) {
        if (categoria.getNome() == null || categoria.getNome().isBlank()) {
            return ResponseEntity.badRequest().body("O nome da categoria é obrigatório.");
        }
        categoria.setId(null);
        if (categoria.getAtiva() == null) {
            categoria.setAtiva(true);
        }
        try {
            Categoria salva = categoriaRepository.save(categoria);
            return ResponseEntity.status(201).body(salva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Já existe uma categoria com esse nome.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Categoria dados) {
        Optional<Categoria> busca = categoriaRepository.findById(id);
        if (busca.isEmpty()) {
            return ResponseEntity.status(404).body("Categoria não encontrada.");
        }
        if (dados.getNome() == null || dados.getNome().isBlank()) {
            return ResponseEntity.badRequest().body("O nome da categoria é obrigatório.");
        }

        Categoria existente = busca.get();
        existente.setNome(dados.getNome());
        existente.setDescricao(dados.getDescricao());
        if (dados.getAtiva() != null) {
            existente.setAtiva(dados.getAtiva());
        }

        try {
            return ResponseEntity.ok(categoriaRepository.save(existente));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar categoria.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (!categoriaRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Categoria não encontrada.");
        }
        try {
            categoriaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Não é possível excluir categoria com produtos cadastrados.");
        }
    }
}
