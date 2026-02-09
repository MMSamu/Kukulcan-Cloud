package com.uamishop.backend.catalogo.domain;

import java.util.UUID;

/**
 * Value Object ProductoId.
 *
 * Representa el identificador único e inmutable de un producto
 * dentro del dominio de Catálogo.
 *
 * Encapsula un UUID para evitar el uso de tipos primitivos
 * y reforzar el lenguaje ubicuo del dominio.
 */
public class ProductoId {

    /** Valor interno del identificador */
    private final UUID valor;

    /**
     * Constructor privado.
     *
     * Evita la creación directa del identificador y fuerza
     * el uso de métodos de fábrica.
     *
     * @param valor UUID del producto
     */
   private ProductoId(UUID valor){
       this.valor = valor;
   }

    /**
     * Genera un nuevo identificador de producto.
     *
     * @return nueva instancia de ProductoId con UUID aleatorio
     */
   public static ProductoId generar(){
       return new ProductoId(UUID.randomUUID());
   }

    /**
     * Obtiene el valor UUID del identificador.
     *
     * @return UUID del producto
     */
   public UUID getValue() {
       return valor;
   }


}
