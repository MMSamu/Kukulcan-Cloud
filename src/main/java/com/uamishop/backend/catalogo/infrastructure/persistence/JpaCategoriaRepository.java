package com.uamishop.backend.catalogo.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaCategoriaRepository
        extends JpaRepository<CategoriaEntity, UUID> {
}