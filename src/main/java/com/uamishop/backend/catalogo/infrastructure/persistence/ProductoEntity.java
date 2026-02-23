/**
 * @file ProductoEntity.java
 * @brief Entidad JPA que representa la tabla "productos".
 *
 * Esta clase pertenece a la capa de infraestructura (Infrastructure Layer)
 * y se encarga exclusivamente de la persistencia de datos.
 *
 * No contiene lógica de negocio.
 *
 * Responsabilidades:
 * - Mapear la tabla "productos" de la base de datos.
 * - Representar el estado persistido del producto.
 * - Ser utilizada por Spring Data JPA para operaciones CRUD.
 *
 * Importante:
 * Esta clase NO debe contener reglas de negocio.
 * Las reglas de negocio pertenecen al dominio.
 */

package com.uamishop.backend.catalogo.infrastructure.persistence;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * @class ProductoEntity
 * @brief Entidad persistente que representa un producto en la base de datos.
 *
 * Anotaciones principales:
 * - @Entity → Indica que esta clase es una entidad JPA.
 * - @Table → Especifica el nombre de la tabla asociada.
 *
 * Esta entidad es utilizada por Hibernate (implementación de JPA)
 * para mapear registros de la tabla "productos".
 */
@Entity
@Table(name = "productos")
public class ProductoEntity {

    /**
     * Identificador único del producto.
     *
     * Se mapea como clave primaria (PRIMARY KEY).
     * El UUID es generado en la capa de dominio.
     */
    @Id
    private UUID id;

    /**
     * Nombre del producto.
     *
     * Representa el título o nombre comercial del producto.
     */
    private String nombre;

    /**
     * Descripción detallada del producto.
     */
    private String descripcion;

    /**
     * Precio del producto.
     *
     * Se utiliza BigDecimal para evitar problemas de precisión
     * en operaciones financieras.
     */
    private BigDecimal precio;

    /**
     * Identificador de la categoría a la que pertenece el producto.
     *
     * Se modela como UUID simple en lugar de usar @ManyToOne
     * para mantener bajo acoplamiento entre entidades.
     */
    private UUID categoriaId;

    /**
     * Indica si el producto está disponible para venta.
     */
    private boolean disponible;

    /**
     * Fecha y hora en la que el producto fue creado.
     *
     * Se almacena como LocalDateTime.
     */
    private LocalDateTime fechaCreacion;

    /**
     * Constructor vacío requerido por JPA.
     *
     * Debe ser protected o public.
     * JPA lo utiliza mediante reflexión para instanciar la entidad.
     */
    protected ProductoEntity() {}

    /**
     * Constructor completo.
     *
     * Se utiliza para crear instancias desde el repositorio
     * al convertir desde el modelo de dominio.
     *
     * @param id identificador único
     * @param nombre nombre del producto
     * @param descripcion descripción del producto
     * @param precio precio del producto
     * @param categoriaId identificador de categoría asociada
     * @param disponible indica disponibilidad
     * @param fechaCreacion fecha de creación
     */
    public ProductoEntity(UUID id,
                          String nombre,
                          String descripcion,
                          BigDecimal precio,
                          UUID categoriaId,
                          boolean disponible,
                          LocalDateTime fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoriaId = categoriaId;
        this.disponible = disponible;
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * @return identificador único del producto
     */
    public UUID getId() {
        return id;
    }

    /**
     * @return nombre del producto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @return descripción del producto
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @return precio del producto
     */
    public BigDecimal getPrecio() {
        return precio;
    }

    /**
     * @return identificador de la categoría
     */
    public UUID getCategoriaId() {
        return categoriaId;
    }

    /**
     * @return true si el producto está disponible
     */
    public boolean isDisponible() {
        return disponible;
    }

    /**
     * @return fecha de creación del producto
     */
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}