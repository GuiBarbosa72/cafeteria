package com.dt2.cafeteria.controller;

import com.dt2.cafeteria.entities.Cliente;
import com.dt2.cafeteria.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if (cliente.isEmpty()) {
            return ResponseEntity.status(404).body("Cliente não encontrado.");
        }
        return ResponseEntity.ok(cliente.get());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().isBlank()) {
            return ResponseEntity.badRequest().body("O nome do cliente é obrigatório.");
        }
        if (cliente.getTelefone() == null || cliente.getTelefone().isBlank()) {
            return ResponseEntity.badRequest().body("O telefone é obrigatório.");
        }
        if (cliente.getEmail() == null || !cliente.getEmail().contains("@")) {
            return ResponseEntity.badRequest().body("Informe um e-mail válido.");
        }

        cliente.setId(null);
        if (cliente.getPontosFidelidade() == null) {
            cliente.setPontosFidelidade(0);
        }

        try {
            Cliente salvo = clienteRepository.save(cliente);
            return ResponseEntity.status(201).body(salvo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Já existe um cliente com esse e-mail.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Cliente dados) {
        Optional<Cliente> busca = clienteRepository.findById(id);
        if (busca.isEmpty()) {
            return ResponseEntity.status(404).body("Cliente não encontrado.");
        }
        if (dados.getNome() == null || dados.getNome().isBlank()) {
            return ResponseEntity.badRequest().body("O nome do cliente é obrigatório.");
        }
        if (dados.getEmail() == null || !dados.getEmail().contains("@")) {
            return ResponseEntity.badRequest().body("Informe um e-mail válido.");
        }

        Cliente existente = busca.get();
        existente.setNome(dados.getNome());
        existente.setTelefone(dados.getTelefone());
        existente.setEmail(dados.getEmail());
        if (dados.getPontosFidelidade() != null) {
            existente.setPontosFidelidade(dados.getPontosFidelidade());
        }

        try {
            return ResponseEntity.ok(clienteRepository.save(existente));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar cliente.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (!clienteRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Cliente não encontrado.");
        }
        try {
            clienteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Não é possível excluir cliente com pedidos registrados.");
        }
    }
}
