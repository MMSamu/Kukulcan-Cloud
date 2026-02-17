package com.uamishop.backend.catalogo.domain;

import com.uamishop.backend.shared.domain.Money;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad de dominio Producto.
 *
 * Representa un producto dentro del catalogo del sistema.
 * Contiene reglas de negocio relacionadas con:
 * - Creación del producto
 * - Manejo de precios
 * - Activación y desactivación
 * - Gestión de imágenes
 *
 * Reglas de negocio aplicadas:
 * RN-CAT-01 a RN-CAT-10
 */

public class Producto {

    /** Identificador unico del producto */
    private final ProductoId id;

    /** Nombre del producto */
    private String nombre;

    /** Descripcion del producto */
    private String descripcion;

    /** Precio del producto */
    private Money precio;

    /** Identificador de la categoría */
    private CategoriaId categoriaId;

    /** Lista de imágenes asociadas al producto */
    private final List<Imagen> imagenes;

    /** Indica si el producto está disponible para la venta */
    private boolean disponible;

    /** Fecha de creación del producto */
    private final LocalDateTime fechaCreacion;

    /**
     * Constructor privado.
     * Se utiliza exclusivamente desde el método de fábrica {@link #crear}.
     *
     * @param id identificador del producto
     * @param nombre nombre del producto
     * @param descripcion descripción del producto
     * @param precio precio del producto
     * @param categoriaId categoría asociada
     */
    private Producto(
            ProductoId id,
            String nombre,
            String descripcion,
            Money precio,
            CategoriaId categoriaId
    ) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoriaId = categoriaId;
        this.imagenes = new ArrayList<>();
        this.disponible = false;
        this.fechaCreacion = LocalDateTime.now();
    }

    /**
     * Crea un nuevo producto aplicando las reglas de negocio.
     *
     * RN-CAT-01: El nombre debe tener entre 3 y 100 caracteres.
     * RN-CAT-02: La descripción no puede exceder 500 caracteres.
     * RN-CAT-03: El precio debe ser mayor a cero.
     *
     * @param nombre nombre del producto
     * @param descripcion descripción del producto
     * @param precio precio del producto
     * @param categoriaId categoría asociada
     * @return producto creado
     * @throws IllegalArgumentException si alguna regla de negocio no se cumple
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
                ProductoId.generar(),
                nombre,
                descripcion,
                precio,
                categoriaId
        );
    }

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
     * Cambia el precio del producto aplicando reglas de negocio.
     *
     * RN-CAT-04: El precio no puede ser negativo.
     * RN-CAT-05: El incremento no puede ser mayor al 50%.
     *
     * @param nuevoPrecio nuevo precio del producto
     * @throws IllegalArgumentException si el precio es inválido
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
     * Agrega una imagen al producto.
     *
     * RN-CAT-06: Un producto no puede tener más de 5 imágenes.
     *
     * @param imagen imagen a agregar
     * @throws IllegalStateException si se excede el límite de imágenes
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
     * RN-CAT-08: No se puede desactivar un producto ya desactivado.
     *
     * @throws IllegalStateException si el producto ya está desactivado
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
     * RN-CAT-09: El producto debe tener al menos una imagen.
     * RN-CAT-10: El producto debe tener un precio válido.
     *
     * @throws IllegalStateException si no se cumplen las reglas de negocio
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

    public ProductoId getId() {

        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Money getPrecio() {
        return precio;
    }

    public CategoriaId getCategoriaId() {
        return categoriaId;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public List<Imagen> getImagenes() {
        return List.copyOf(imagenes);
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }


}


//checando errores
