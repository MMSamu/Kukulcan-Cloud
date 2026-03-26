package com.uamishop.ventas.repository;

import com.uamishop.ventas.domain.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

// Repositorio JPA para la entidad Carrito, que extiende JpaRepository para
// proporcionar operaciones CRUD básicas.
@Repository
public interface CarritoJpaRepository extends JpaRepository<Carrito, UUID> {
}