package com.dt2.cafeteria.controller;

import com.dt2.cafeteria.entities.Cliente;
import com.dt2.cafeteria.entities.Mesa;
import com.dt2.cafeteria.entities.Pedido;
import com.dt2.cafeteria.entities.Produto;
import com.dt2.cafeteria.repository.ClienteRepository;
import com.dt2.cafeteria.repository.MesaRepository;
import com.dt2.cafeteria.repository.PedidoRepository;
import com.dt2.cafeteria.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping
    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        if (pedido.isEmpty()) {
            return ResponseEntity.status(404).body("Pedido não encontrado.");
        }
        return ResponseEntity.ok(pedido.get());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Pedido pedido) {
        if (pedido.getCliente() == null || pedido.getCliente().getId() == null) {
            return ResponseEntity.badRequest().body("Selecione um cliente.");
        }
        if (pedido.getMesa() == null || pedido.getMesa().getId() == null) {
            return ResponseEntity.badRequest().body("Selecione uma mesa.");
        }
        if (pedido.getProduto() == null || pedido.getProduto().getId() == null) {
            return ResponseEntity.badRequest().body("Selecione um produto.");
        }
        if (pedido.getQuantidade() == null || pedido.getQuantidade() < 1) {
            return ResponseEntity.badRequest().body("A quantidade mínima é 1.");
        }

        Optional<Cliente> clienteBusca = clienteRepository.findById(pedido.getCliente().getId());
        if (clienteBusca.isEmpty()) {
            return ResponseEntity.status(404).body("Cliente não encontrado.");
        }

        Optional<Mesa> mesaBusca = mesaRepository.findById(pedido.getMesa().getId());
        if (mesaBusca.isEmpty()) {
            return ResponseEntity.status(404).body("Mesa não encontrada.");
        }

        Optional<Produto> produtoBusca = produtoRepository.findById(pedido.getProduto().getId());
        if (produtoBusca.isEmpty()) {
            return ResponseEntity.status(404).body("Produto não encontrado.");
        }

        Produto produto = produtoBusca.get();
        if (produto.getDisponivel() == null || !produto.getDisponivel()) {
            return ResponseEntity.badRequest().body("O produto '" + produto.getNome() + "' está indisponível.");
        }

        Cliente cliente = clienteBusca.get();
        Mesa mesa = mesaBusca.get();

        // Calcula o valor total e os pontos (1 ponto a cada R$ 10,00)
        Double total = produto.getPreco() * pedido.getQuantidade();
        int pontosGanhos = (int) (total / 10);

        cliente.setPontosFidelidade(cliente.getPontosFidelidade() + pontosGanhos);
        clienteRepository.save(cliente);

        mesa.setStatus("OCUPADA");
        mesaRepository.save(mesa);

        pedido.setId(null);
        pedido.setCliente(cliente);
        pedido.setMesa(mesa);
        pedido.setProduto(produto);
        pedido.setValorTotal(total);
        pedido.setDataHora(LocalDateTime.now());
        if (pedido.getStatus() == null || pedido.getStatus().isBlank()) {
            pedido.setStatus("ABERTO");
        }

        Pedido salvo = pedidoRepository.save(pedido);
        return ResponseEntity.status(201).body(salvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Pedido dados) {
        Optional<Pedido> busca = pedidoRepository.findById(id);
        if (busca.isEmpty()) {
            return ResponseEntity.status(404).body("Pedido não encontrado.");
        }
        if (dados.getQuantidade() == null || dados.getQuantidade() < 1) {
            return ResponseEntity.badRequest().body("A quantidade mínima é 1.");
        }

        Optional<Cliente> clienteBusca = clienteRepository.findById(dados.getCliente().getId());
        Optional<Mesa> mesaBusca = mesaRepository.findById(dados.getMesa().getId());
        Optional<Produto> produtoBusca = produtoRepository.findById(dados.getProduto().getId());

        if (clienteBusca.isEmpty() || mesaBusca.isEmpty() || produtoBusca.isEmpty()) {
            return ResponseEntity.status(404).body("Cliente, mesa ou produto não encontrado.");
        }

        Produto produto = produtoBusca.get();
        if (produto.getDisponivel() == null || !produto.getDisponivel()) {
            return ResponseEntity.badRequest().body("O produto '" + produto.getNome() + "' está indisponível.");
        }

        Pedido existente = busca.get();
        existente.setCliente(clienteBusca.get());
        existente.setMesa(mesaBusca.get());
        existente.setProduto(produto);
        existente.setQuantidade(dados.getQuantidade());
        existente.setObservacoes(dados.getObservacoes());
        existente.setValorTotal(produto.getPreco() * dados.getQuantidade());
        if (dados.getStatus() != null) {
            existente.setStatus(dados.getStatus());
        }

        return ResponseEntity.ok(pedidoRepository.save(existente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        if (!pedidoRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Pedido não encontrado.");
        }
        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
