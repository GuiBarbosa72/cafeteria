package com.dt2.cafeteria.controller;

import com.dt2.cafeteria.entities.Mesa;
import com.dt2.cafeteria.repository.MesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {

    @Autowired
    private MesaRepository mesaRepository;

    @GetMapping
    public List<Mesa> listar() {
        return mesaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Mesa> mesa = mesaRepository.findById(id);
        if (mesa.isEmpty()) {
            return ResponseEntity.status(404).body("Mesa não encontrada.");
        }
        return ResponseEntity.ok(mesa.get());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Mesa mesa) {
        if (mesa.getNumero() == null || mesa.getNumero() <= 0) {
            return ResponseEntity.badRequest().body("O número da mesa é obrigatório.");
        }
        if (mesa.getCapacidade() == null || mesa.getCapacidade() < 1) {
            return ResponseEntity.badRequest().body("A capacidade mínima é de 1 lugar.");
        }
        if (mesa.getStatus() == null || mesa.getStatus().isBlank()) {
            mesa.setStatus("LIVRE");
        }

        mesa.setId(null);
        try {
            Mesa salva = mesaRepository.save(mesa);
            return ResponseEntity.status(201).body(salva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Já existe uma mesa com esse número.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Mesa dados) {
        Optional<Mesa> busca = mesaRepository.findById(id);
        if (busca.isEmpty()) {
            return ResponseEntity.status(404).body("Mesa não encontrada.");
        }
        if (dados.getNumero() == null || dados.getNumero() <= 0) {
            return ResponseEntity.badRequest().body("O número da mesa é obrigatório.");
        }
        if (dados.getCapacidade() == null || dados.getCapacidade() < 1) {
            return ResponseEntity.badRequest().body("A capacidade mínima é de 1 lugar.");
        }

        Mesa existente = busca.get();
        existente.setNumero(dados.getNumero());
        existente.setCapacidade(dados.getCapacidade());
        if (dados.getStatus() != null) {
            existente.setStatus(dados.getStatus());
        }

        try {
            return ResponseEntity.ok(mesaRepository.save(existente));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar mesa.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (!mesaRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Mesa não encontrada.");
        }
        try {
            mesaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Não é possível excluir mesa vinculada a pedidos.");
        }
    }
}
