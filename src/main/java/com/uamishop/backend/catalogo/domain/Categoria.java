/**
 * @file Categoria.java
 * @brief Entidad de dominio que representa una categoría dentro del sistema.
 *
 * Esta clase pertenece a la capa de dominio (Domain Layer).
 * Contiene reglas de negocio básicas como validaciones al crear o actualizar.
 */
package com.uamishop.backend.catalogo.domain;

import com.uamishop.backend.shared.domain.CategoriaId;

/**
 * @class Categoria
 * @brief Representa una categoría del catálogo.
 *
 * Una categoría puede:
 * - Tener un identificador único (CategoriaId)
 * - Tener nombre y descripción
 * - Tener una categoría padre (jerarquía)
 *
 * Esta clase encapsula reglas de negocio y protege la integridad del objeto.
 */
public class Categoria {

    /**
     * Identificador único de la categoría.
     * Es final porque no debe cambiar después de crear el objeto.
     */
    private final CategoriaId id;

    /**
     * Nombre de la categoría.
     */
    private String nombre;

    /**
     * Descripción de la categoría.
     */
    private String descripcion;

    /**
     * Identificador de la categoría padre.
     * Permite crear jerarquías (subcategorías).
     */
    private CategoriaId categoriaPadreId;

    /**
     * Constructor que crea una nueva categoría.
     *
     * @param id Identificador único
     * @param nombre Nombre de la categoría (obligatorio)
     * @param descripcion Descripción de la categoría
     *
     * @throws IllegalArgumentException si el nombre es nulo o vacío
     */
    public Categoria(CategoriaId id, String nombre, String descripcion) {

        // Validación de regla de negocio:
        // El nombre no puede ser nulo ni estar vacío
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        // Asignación de valores
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    /**
     * Método para actualizar los datos de la categoría.
     *
     * @param nombre Nuevo nombre
     * @param descripcion Nueva descripción
     *
     * @throws IllegalArgumentException si el nombre es inválido
     */
    public void actualizar(String nombre, String descripcion) {

        // Regla de negocio: el nombre no puede ser vacío
        if(nombre == null || nombre.isBlank()){
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }

        // NOTA: Actualmente el método no asigna los nuevos valores.
        // Faltaría:
        // this.nombre = nombre;
        // this.descripcion = descripcion;
    }

    /**
     * Asigna una categoría padre.
     *
     * @param padreId Identificador de la categoría padre
     */
    public void asignarPadre(CategoriaId padreId) {

        // Establece la relación jerárquica
        this.categoriaPadreId = padreId;
    }

    /**
     * Obtiene el identificador de la categoría.
     *
     * @return CategoriaId
     */
    public CategoriaId getId() {
        return id;
    }

    /**
     * Obtiene el nombre de la categoría.
     *
     * @return nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene la descripción de la categoría.
     *
     * @return descripción
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Obtiene el identificador de la categoría padre.
     *
     * @return CategoriaId padre
     */
    public CategoriaId getCategoriaPadreId() {
        return categoriaPadreId;
    }
}
