package com.dt2.cafeteria.repository;

import com.dt2.cafeteria.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
