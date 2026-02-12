package com.uamishop.backend.ventas.repository;

import com.uamishop.backend.ventas.domain.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface CarritoJpaRepository extends JpaRepository<Carrito, UUID> {
}
