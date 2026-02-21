package com.uamishop.backend.catalogo.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaProductoRepository
        extends JpaRepository<ProductoEntity, UUID> {

    List<ProductoEntity> findByCategoriaId(UUID categoriaId);
}
