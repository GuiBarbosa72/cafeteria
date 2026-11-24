package com.dt2.cafeteria.repository;

import com.dt2.cafeteria.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
