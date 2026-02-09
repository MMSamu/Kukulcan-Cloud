package com.uamishop.backend.catalogo.domain;

/**
 * Entidad de dominio Categoria.
 *
 * Representa una categoría dentro del catálogo de productos.
 * Las categorías pueden organizarse jerárquicamente mediante
 * una relación padre-hijo.
 *
 * Reglas de negocio:
 * - Una categoría puede tener una categoría padre.
 * - El nombre y la descripción pueden actualizarse.
 */

public class Categoria {

    /** Identificador único de la categoría */
    private final CategoriaId id;

    /** Nombre de la categoría */
    private String nombre;

    /** Descripción de la categoría */
    private String descripcion;

    /** Identificador de la categoría padre (opcional) */
    private CategoriaId categoriaPadreId;


    /**
     * Constructor de la categoría.
     *
     * @param id identificador único de la categoría
     * @param nombre nombre de la categoría
     * @param descripcion descripción de la categoría
     */
    public Categoria(CategoriaId id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    /**
     * Actualiza la información de la categoría.
     *
     * @param nombre nuevo nombre de la categoría
     * @param descripcion nueva descripción de la categoría
     */
    public void actualizar(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    /**
     * Asigna una categoría padre.
     *
     * Permite construir una jerarquía de categorías.
     *
     * @param padreId identificador de la categoría padre
     */
    public void asignarPadre(CategoriaId padreId) {
        this.categoriaPadreId = padreId;
    }

    /**
     * Obtiene el identificador de la categoría.
     *
     * @return identificador de la categoría
     */
    public CategoriaId getId() {
        return id;
    }
}
