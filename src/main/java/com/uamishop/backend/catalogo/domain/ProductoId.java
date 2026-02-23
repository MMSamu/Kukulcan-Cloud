/**
 * @file ProductoId.java
 * @brief Value Object que representa la identidad única de un Producto.
 *
 * Pertenece a la capa Domain.
 * Encapsula un UUID para evitar usar tipos primitivos directamente en el modelo.
 *
 * Beneficios:
 * - Seguridad de tipos
 * - Mejor expresividad del dominio
 * - Encapsulamiento de validaciones
 */
package com.uamishop.backend.catalogo.domain;

// Utilidad para validaciones de null
import java.util.Objects;

// Identificador único universal
import java.util.UUID;

/**
 * @class ProductoId
 * @brief Value Object que encapsula el identificador del Producto.
 *
 * En DDD:
 * - Aunque identifica una entidad, sigue siendo un Value Object.
 * - Es inmutable.
 * - Se compara por valor.
 */
public class ProductoId {

    /** UUID interno que representa el identificador */
    private final UUID valor;

    /**
     * Constructor principal.
     *
     * Valida que el UUID no sea nulo.
     *
     * @param valor UUID del producto
     * @throws NullPointerException si el valor es null
     */
    public ProductoId(UUID valor) {

        // requireNonNull lanza NullPointerException si es null
        this.valor = Objects.requireNonNull(valor);
    }

    /**
     * Método fábrica para generar un nuevo identificador.
     *
     * @return nueva instancia de ProductoId con UUID aleatorio
     */
    public static ProductoId generar() {

        // UUID.randomUUID() crea un identificador único universal
        return new ProductoId(UUID.randomUUID());
    }

    /**
     * Obtiene el UUID encapsulado.
     *
     * @return valor interno UUID
     */
    public UUID valor() {
        return valor;
    }

    /**
     * Compara dos ProductoId por valor.
     *
     * Dos identificadores son iguales si contienen el mismo UUID.
     *
     * @param o objeto a comparar
     * @return true si representan el mismo ID
     */
    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof ProductoId that)) return false;

        return valor.equals(that.valor);
    }

    /**
     * Genera el hash basado en el UUID.
     *
     * Obligatorio cuando se sobrescribe equals.
     *
     * @return hash del identificador
     */
    @Override
    public int hashCode() {
        return valor.hashCode();
    }

    /**
     * Representación en texto del identificador.
     *
     * Útil para logs, debugging y serialización.
     *
     * @return UUID en formato String
     */
    @Override
    public String toString() {
        return valor.toString();
    }
}