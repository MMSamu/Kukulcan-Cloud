/**
 * @file CategoriaRepositoryImpl.java
 * @brief Implementación concreta del repositorio de Categoría usando JPA.
 *
 * Pertenece a la capa Infrastructure.
 * Implementa la interfaz del dominio CategoriaRepository.
 *
 * Responsabilidad:
 * - Traducir entre objetos del Dominio y entidades JPA.
 * - Delegar operaciones CRUD al JpaCategoriaRepository.
 *
 * Aplica el patrón Repository + Adapter.
 */
package com.uamishop.backend.catalogo.infrastructure.persistence;

import com.uamishop.backend.catalogo.domain.Categoria;
import com.uamishop.backend.shared.domain.CategoriaId;
import com.uamishop.backend.catalogo.repository.CategoriaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @class CategoriaRepositoryImpl
 * @brief Adaptador entre el dominio y la infraestructura de persistencia.
 *
 * Esta clase:
 * - Implementa la interfaz definida en el dominio.
 * - Usa un repositorio JPA para acceder a la base de datos.
 * - Convierte entre Categoria (dominio) y CategoriaEntity (persistencia).
 */
@Repository
public class CategoriaRepositoryImpl implements CategoriaRepository {

    /** Repositorio JPA que interactúa con la base de datos */
    private final JpaCategoriaRepository jpaRepository;

    /**
     * Constructor con inyección de dependencia.
     *
     * @param jpaRepository repositorio Spring Data JPA
     */
    public CategoriaRepositoryImpl(JpaCategoriaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * Guarda una categoría en la base de datos.
     *
     * Convierte el objeto de dominio a entidad JPA antes de persistir.
     *
     * @param categoria objeto del dominio
     * @return la misma categoría persistida
     */
    @Override
    public Categoria save(Categoria categoria) {

        CategoriaEntity entity = toEntity(categoria);
        jpaRepository.save(entity);

        return categoria;
    }

    /**
     * Busca una categoría por su identificador.
     *
     * Convierte la entidad JPA a objeto del dominio.
     *
     * @param id identificador del dominio
     * @return Optional con la categoría si existe
     */
    @Override
    public Optional<Categoria> findById(CategoriaId id) {

        return jpaRepository.findById(id.valor())
                .map(this::toDomain);
    }

    /**
     * Obtiene todas las categorías.
     *
     * Convierte cada entidad a objeto de dominio.
     *
     * @return lista de categorías del dominio
     */
    @Override
    public List<Categoria> findAll() {

        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Elimina una categoría por ID.
     *
     * @param id identificador del dominio
     */
    @Override
    public void deleteById(CategoriaId id) {
        jpaRepository.deleteById(id.valor());
    }

    /**
     * Verifica si existe una categoría por ID.
     *
     * @param id identificador del dominio
     * @return true si existe
     */
    @Override
    public boolean existsById(CategoriaId id) {
        return jpaRepository.existsById(id.valor());
    }

    // =============================
    // MAPPERS
    // =============================

    /**
     * Convierte un objeto del dominio a entidad JPA.
     *
     * @param categoria objeto del dominio
     * @return entidad lista para persistir
     */
    private CategoriaEntity toEntity(Categoria categoria) {
        return new CategoriaEntity(
                categoria.getId().valor(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getCategoriaPadreId() != null
                        ? categoria.getCategoriaPadreId().valor()
                        : null
        );
    }

    /**
     * Convierte una entidad JPA a objeto del dominio.
     *
     * @param entity entidad obtenida desde base de datos
     * @return objeto del dominio Categoria
     */
    private Categoria toDomain(CategoriaEntity entity) {

        Categoria categoria = new Categoria(
                new CategoriaId(entity.getId()),
                entity.getNombre(),
                entity.getDescripcion()
        );

        // Asigna categoría padre si existe
        if (entity.getCategoriaPadreId() != null) {
            categoria.asignarPadre(
                    new CategoriaId(entity.getCategoriaPadreId())
            );
        }

        return categoria;
    }
}

