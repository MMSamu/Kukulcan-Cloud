package com.uamishop.catalogo.repository;

import com.uamishop.catalogo.domain.Categoria;
import com.uamishop.catalogo.shared.domain.CategoriaId;

import java.util.List;
import java.util.Optional;

/**
 * @interface CategoriaRepository
 *
 * Contrato que define las operaciones de persistencia
 * para el agregado raíz Categoria.
 *
 * Las implementaciones concretas estarán en la capa
 * de infraestructura.
 */
public interface CategoriaRepository {

    /**
     * Guarda una categoría.
     *
     * Puede representar:
     * - Inserción (si no existe previamente)
     * - Actualización (si ya existe)
     *
     * @param categoria agregado de dominio Categoria
     * @return categoría persistida
     */
    Categoria save(Categoria categoria);

    /**
     * Busca una categoría por su identificador.
     *
     * @param id identificador de categoría (Value Object)
     * @return Optional con la categoría si existe
     */
    Optional<Categoria> findById(CategoriaId id);

    /**
     * Obtiene todas las categorías almacenadas.
     *
     * @return lista de categorías
     */
    List<Categoria> findAll();

    /**
     * Elimina una categoría por su identificador.
     *
     * @param id identificador de categoría
     */
    void deleteById(CategoriaId id);

    /**
     * Verifica si una categoría existe.
     *
     * @param id identificador de categoría
     * @return true si existe, false en caso contrario
     */
    boolean existsById(CategoriaId id);
}