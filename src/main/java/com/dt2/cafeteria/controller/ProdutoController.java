package com.dt2.cafeteria.controller;

import com.dt2.cafeteria.entities.Categoria;
import com.dt2.cafeteria.entities.Produto;
import com.dt2.cafeteria.repository.CategoriaRepository;
import com.dt2.cafeteria.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public List<Produto> listar() {
        return produtoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Produto> produto = produtoRepository.findById(id);
        if (produto.isEmpty()) {
            return ResponseEntity.status(404).body("Produto não encontrado.");
        }
        return ResponseEntity.ok(produto.get());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Produto produto) {
        if (produto.getNome() == null || produto.getNome().isBlank()) {
            return ResponseEntity.badRequest().body("O nome do produto é obrigatório.");
        }
        if (produto.getPreco() == null || produto.getPreco() <= 0) {
            return ResponseEntity.badRequest().body("O preço deve ser maior que zero.");
        }
        if (produto.getCategoria() == null || produto.getCategoria().getId() == null) {
            return ResponseEntity.badRequest().body("A categoria é obrigatória.");
        }

        Optional<Categoria> categoria = categoriaRepository.findById(produto.getCategoria().getId());
        if (categoria.isEmpty()) {
            return ResponseEntity.status(404).body("Categoria não encontrada.");
        }

        produto.setId(null);
        produto.setCategoria(categoria.get());
        if (produto.getDisponivel() == null) {
            produto.setDisponivel(true);
        }

        Produto salvo = produtoRepository.save(produto);
        return ResponseEntity.status(201).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Produto dados) {
        Optional<Produto> busca = produtoRepository.findById(id);
        if (busca.isEmpty()) {
            return ResponseEntity.status(404).body("Produto não encontrado.");
        }
        if (dados.getNome() == null || dados.getNome().isBlank()) {
            return ResponseEntity.badRequest().body("O nome do produto é obrigatório.");
        }
        if (dados.getPreco() == null || dados.getPreco() <= 0) {
            return ResponseEntity.badRequest().body("O preço deve ser maior que zero.");
        }
        if (dados.getCategoria() == null || dados.getCategoria().getId() == null) {
            return ResponseEntity.badRequest().body("A categoria é obrigatória.");
        }

        Optional<Categoria> categoria = categoriaRepository.findById(dados.getCategoria().getId());
        if (categoria.isEmpty()) {
            return ResponseEntity.status(404).body("Categoria não encontrada.");
        }

        Produto existente = busca.get();
        existente.setNome(dados.getNome());
        existente.setDescricao(dados.getDescricao());
        existente.setPreco(dados.getPreco());
        existente.setCategoria(categoria.get());
        if (dados.getDisponivel() != null) {
            existente.setDisponivel(dados.getDisponivel());
        }

        return ResponseEntity.ok(produtoRepository.save(existente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (!produtoRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Produto não encontrado.");
        }
        try {
            produtoRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Não é possível excluir produto que já está em pedidos.");
        }
    }
}
