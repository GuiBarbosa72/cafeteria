package com.dt2.cafeteria.repository;

import com.dt2.cafeteria.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
