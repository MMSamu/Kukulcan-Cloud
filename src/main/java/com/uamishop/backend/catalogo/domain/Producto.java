/**
 * @file Producto.java
 * @brief Entidad de dominio que representa un producto del catálogo.
 *
 * Pertenece a la capa Domain.
 * Implementa reglas de negocio del módulo de catálogo.
 *
 * Diferencia clave:
 * - Es una ENTIDAD (tiene identidad propia: ProductoId).
 * - Tiene estado mutable controlado por reglas de negocio.
 *
 * Reglas aplicadas:
 * RN-CAT-01 a RN-CAT-10
 */
package com.uamishop.backend.catalogo.domain;

// Value Object que representa dinero
import com.uamishop.backend.shared.domain.Money;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @class Producto
 * @brief Entidad principal del agregado Producto.
 *
 * Responsabilidades:
 * - Validar reglas al crearse.
 * - Controlar cambios de estado.
 * - Gestionar imágenes.
 * - Controlar activación/desactivación.
 *
 * Esta clase encapsula TODA la lógica del negocio relacionada al producto.
 */
public class Producto {

    /** Identificador único (identidad de la entidad) */
    private final ProductoId id;

    /** Nombre del producto */
    private String nombre;

    /** Descripción del producto */
    private String descripcion;

    /** Precio representado como Value Object Money */
    private Money precio;

    /** Identificador de la categoría asociada */
    private CategoriaId categoriaId;

    /** Lista interna mutable de imágenes */
    private final List<Imagen> imagenes;

    /** Indica si el producto está disponible para venta */
    private boolean disponible;

    /** Fecha de creación (inmutable) */
    private final LocalDateTime fechaCreacion;

    /**
     * Constructor privado.
     *
     * Se usa únicamente desde métodos fábrica.
     * Evita crear objetos sin aplicar reglas.
     */
    private Producto(
            ProductoId id,
            String nombre,
            String descripcion,
            Money precio,
            CategoriaId categoriaId,
            boolean disponible,
            LocalDateTime fechaCreacion
    ) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoriaId = categoriaId;
        this.imagenes = new ArrayList<>();
        this.disponible = disponible;
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Método fábrica para crear un nuevo producto.
     *
     * Aplica reglas:
     * RN-CAT-01: Nombre entre 3 y 100 caracteres.
     * RN-CAT-02: Descripción máximo 500 caracteres.
     * RN-CAT-03: Precio mayor a cero.
     *
     * @return nueva instancia válida de Producto
     */
    public static Producto crear(
            String nombre,
            String descripcion,
            Money precio,
            CategoriaId categoriaId
    ) {

        if (nombre.length() < 3 || nombre.length() > 100) {
            throw new IllegalArgumentException("El nombre debe tener entre 3 y 100 caracteres");
        }

        if (descripcion.length() > 500) {
            throw new IllegalArgumentException("La descripción no puede exceder 500 caracteres");
        }

        if (precio.getCantidad().doubleValue() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero");
        }

        return new Producto(
                ProductoId.generar(),        // Genera identidad
                nombre,
                descripcion,
                precio,
                categoriaId,
                false,                       // Inicia desactivado
                LocalDateTime.now()          // Fecha actual
        );
    }

    /**
     * Método de reconstrucción.
     *
     * Se usa cuando el objeto viene desde base de datos.
     * NO aplica reglas porque se asume ya validado.
     */
    public static Producto reconstruir(
            ProductoId id,
            String nombre,
            String descripcion,
            Money precio,
            CategoriaId categoriaId,
            boolean disponible,
            LocalDateTime fechaCreacion
    ) {
        return new Producto(
                id,
                nombre,
                descripcion,
                precio,
                categoriaId,
                disponible,
                fechaCreacion
        );
    }

    /**
     * Actualiza nombre y descripción aplicando reglas.
     */
    public void actualizarNombreYDescripcion(String nombre, String descripcion) {

        if (nombre == null || nombre.length() < 3 || nombre.length() > 100) {
            throw new IllegalArgumentException("El nombre debe tener entre 3 y 100 caracteres");
        }

        if (descripcion == null || descripcion.length() > 500) {
            throw new IllegalArgumentException("La descripción no puede exceder 500 caracteres");
        }

        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    /**
     * Cambia el precio del producto.
     *
     * RN-CAT-04: No puede ser negativo.
     * RN-CAT-05: No puede aumentar más del 50%.
     */
    public void cambiarPrecio(Money nuevoPrecio) {

        if (nuevoPrecio.getCantidad().doubleValue() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }

        double incremento = nuevoPrecio.getCantidad()
                .subtract(precio.getCantidad())
                .divide(precio.getCantidad())
                .doubleValue();

        if (incremento > 0.5) {
            throw new IllegalArgumentException("El precio no puede aumentar más del 50%");
        }

        this.precio = nuevoPrecio;
    }

    /**
     * Agrega una imagen.
     *
     * RN-CAT-06: Máximo 5 imágenes.
     */
    public void agregarImagen(Imagen imagen) {

        if (imagenes.size() >= 5) {
            throw new IllegalStateException("Un producto no puede tener más de 5 imágenes");
        }

        imagenes.add(imagen);
    }

    /**
     * Desactiva el producto.
     *
     * RN-CAT-08: No se puede desactivar si ya está desactivado.
     */
    public void desactivar() {

        if (!disponible) {
            throw new IllegalStateException("El producto ya está desactivado");
        }

        this.disponible = false;
    }

    /**
     * Activa el producto.
     *
     * RN-CAT-09: Debe tener al menos una imagen.
     * RN-CAT-10: Debe tener precio válido.
     */
    public void activar() {

        if (imagenes.isEmpty()) {
            throw new IllegalStateException("El producto debe tener al menos una imagen");
        }

        if (precio.getCantidad().doubleValue() <= 0) {
            throw new IllegalStateException("El producto debe tener un precio válido");
        }

        this.disponible = true;
    }

    /* ================= GETTERS ================= */

    public ProductoId getId() { return id; }

    public String getNombre() { return nombre; }

    public String getDescripcion() { return descripcion; }

    public Money getPrecio() { return precio; }

    public CategoriaId getCategoriaId() { return categoriaId; }

    public boolean isDisponible() { return disponible; }

    /**
     * Devuelve copia inmutable para proteger encapsulamiento.
     */
    public List<Imagen> getImagenes() {
        return List.copyOf(imagenes);
    }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}


