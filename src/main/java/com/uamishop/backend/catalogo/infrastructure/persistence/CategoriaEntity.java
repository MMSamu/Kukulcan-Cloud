/**
 * @file CategoriaEntity.java
 * @brief Entidad de persistencia JPA que representa la tabla "categorias".
 *
 * Pertenece a la capa Infrastructure.
 * Se utiliza exclusivamente para la interacción con la base de datos.
 *
 * ⚠ IMPORTANTE:
 * No contiene reglas de negocio.
 * Es un modelo anémico usado solo para persistencia.
 */
package com.uamishop.backend.catalogo.infrastructure.persistence;

// Anotaciones JPA
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * @class CategoriaEntity
 * @brief Representa la tabla "categorias" en la base de datos.
 *
 * Se mapea directamente con la tabla:
 *
 *  categorias
 *  ├── id (UUID)
 *  ├── nombre (String)
 *  ├── descripcion (String)
 *  └── categoria_padre_id (UUID)
 *
 * Esta clase es distinta a la entidad de dominio Categoria.
 */
@Entity
@Table(name = "categorias")
public class CategoriaEntity {

    /**
     * Identificador primario de la categoría.
     * Se mapea como @Id en la base de datos.
     */
    @Id
    private UUID id;

    /** Nombre almacenado en la base de datos */
    private String nombre;

    /** Descripción almacenada en la base de datos */
    private String descripcion;

    /** Identificador de categoría padre (relación jerárquica) */
    private UUID categoriaPadreId;

    /**
     * Constructor vacío requerido por JPA.
     *
     * JPA necesita este constructor para:
     * - Crear instancias mediante reflexión
     * - Hidratar objetos desde la base de datos
     *
     * Debe ser protected o public.
     */
    protected CategoriaEntity() {
    }

    /**
     * Constructor completo para crear la entidad manualmente.
     *
     * @param id identificador UUID
     * @param nombre nombre de la categoría
     * @param descripcion descripción
     * @param categoriaPadreId UUID de categoría padre
     */
    public CategoriaEntity(UUID id, String nombre, String descripcion, UUID categoriaPadreId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoriaPadreId = categoriaPadreId;
    }

    /** @return UUID identificador */
    public UUID getId() {
        return id;
    }

    /** @return nombre almacenado */
    public String getNombre() {
        return nombre;
    }

    /** @return descripción almacenada */
    public String getDescripcion() {
        return descripcion;
    }

    /** @return UUID de la categoría padre */
    public UUID getCategoriaPadreId() {
        return categoriaPadreId;
    }
}
