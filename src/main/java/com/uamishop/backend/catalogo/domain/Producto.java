package com.uamishop.backend.catalogo.domain;

import com.uamishop.backend.shared.domain.Money;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Producto {

    private final ProductoId id;
    private String nombre;
    private String descripcion;
    private Money precio;
    private CategoriaId categoriaId;
    private final List<Imagen> imagenes;
    private boolean disponible;
    private final LocalDateTime fechaCreacion;

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

    // RN-CAT-01, RN-CAT-02, RN-CAT-03
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

    // RN-CAT-04, RN-CAT-05
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

    // RN-CAT-06, RN-CAT-07
    public void agregarImagen(Imagen imagen) {
        if (imagenes.size() >= 5) {
            throw new IllegalStateException("Un producto no puede tener más de 5 imágenes");
        }
        imagenes.add(imagen);
    }

    // RN-CAT-08
    public void desactivar() {
        if (!disponible) {
            throw new IllegalStateException("El producto ya está desactivado");
        }
        this.disponible = false;
    }

    // RN-CAT-09, RN-CAT-10
    public void activar() {
        if (imagenes.isEmpty()) {
            throw new IllegalStateException("El producto debe tener al menos una imagen");
        }
        if (precio.getCantidad().doubleValue() <= 0) {
            throw new IllegalStateException("El producto debe tener un precio válido");
        }
        this.disponible = true;
    }
}
