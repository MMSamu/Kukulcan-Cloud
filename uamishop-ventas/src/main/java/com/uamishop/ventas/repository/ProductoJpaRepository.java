package com.uamishop.ventas.repository;

import com.uamishop.ventas.domain.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProductoJpaRepository extends JpaRepository<Producto, UUID> {
}