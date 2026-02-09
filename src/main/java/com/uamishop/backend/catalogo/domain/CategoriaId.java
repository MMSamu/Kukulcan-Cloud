package com.uamishop.backend.catalogo.domain;

import java.util.UUID;


/**
 * Value Object CategoriaId.
 *
 * Representa el identificador único e inmutable de una categoría
 * dentro del dominio de Catálogo.
 *
 * Encapsula un UUID para evitar el uso de tipos primitivos
 * y reforzar el lenguaje ubicuo del dominio.
 */
public class CategoriaId {

    /** Valor interno del identificador */
    private final UUID valor;

    /**
     * Constructor privado.
     *
     * Evita la creación directa del identificador y fuerza
     * el uso de métodos de fábrica.
     *
     * @param valor UUID de la categoría
     */
    private CategoriaId(UUID valor){
        this.valor = valor;
    }

    /**
     * Genera un nuevo identificador de categoría.
     *
     * @return nueva instancia de CategoriaId con UUID aleatorio
     */
    public static CategoriaId generar(){
        return new CategoriaId(UUID.randomUUID());
    }

    /**
     * Obtiene el valor UUID del identificador.
     *
     * @return UUID de la categoría
     */
    public UUID getValue(){
        return valor;
    }
}
