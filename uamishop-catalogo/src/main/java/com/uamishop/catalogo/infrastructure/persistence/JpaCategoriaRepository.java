package com.uamishop.catalogo.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * @interface JpaCategoriaRepository
 * @brief Repositorio Spring Data JPA para CategoriaEntity.
 *
 * Al extender JpaRepository<CategoriaEntity, UUID>,
 * Spring genera automáticamente una implementación en tiempo de ejecución.
 *
 * Tipos genéricos:
 * - CategoriaEntity → Tipo de entidad JPA
 * - UUID → Tipo del identificador primario
 *
 * Métodos heredados automáticamente:
 * - save()
 * - findById()
 * - findAll()
 * - deleteById()
 * - existsById()
 * - count()
 * - entre otros
 */
public interface JpaCategoriaRepository
        extends JpaRepository<CategoriaEntity, UUID> {
}