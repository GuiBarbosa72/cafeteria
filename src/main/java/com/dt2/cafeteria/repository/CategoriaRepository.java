package com.dt2.cafeteria.repository;

import com.dt2.cafeteria.entities.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
