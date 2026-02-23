/**
 * @file JpaProductoRepository.java
 * @brief Repositorio JPA para la entidad ProductoEntity.
 *
 * Pertenece a la capa Infrastructure.
 * Extiende JpaRepository para obtener operaciones CRUD automáticas.
 *
 * Además, define consultas derivadas basadas en el nombre del método.
 */
package com.uamishop.backend.catalogo.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * @interface JpaProductoRepository
 * @brief Repositorio Spring Data JPA para ProductoEntity.
 *
 * Al extender JpaRepository<ProductoEntity, UUID>,
 * Spring genera automáticamente la implementación en tiempo de ejecución.
 *
 * Tipos genéricos:
 * - ProductoEntity → Entidad JPA asociada a la tabla productos.
 * - UUID → Tipo del identificador primario.
 *
 * Métodos heredados automáticamente:
 * - save()
 * - findById()
 * - findAll()
 * - deleteById()
 * - existsById()
 *
 * Además incluye un método de consulta personalizada
 * basado en convención de nombres.
 */
public interface JpaProductoRepository
        extends JpaRepository<ProductoEntity, UUID> {

    /**
     * Busca productos por categoría.
     *
     * Spring Data JPA interpreta el nombre del método
     * y genera automáticamente la consulta equivalente a:
     *
     * SELECT * FROM productos WHERE categoria_id = ?
     *
     * @param categoriaId identificador de la categoría
     * @return lista de productos pertenecientes a esa categoría
     */
    List<ProductoEntity> findByCategoriaId(UUID categoriaId);
}
