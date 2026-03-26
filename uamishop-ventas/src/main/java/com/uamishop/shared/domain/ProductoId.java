package com.uamishop.shared.domain;

// Utilidad para validaciones de null
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

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
//Se adapto la clase para que cumpla con los requisitos de JPA
@Embeddable
//Se agrego Embeddable
public class ProductoId {

    @Column(name = "producto_id")
    private UUID valor;

    // 🔥 Constructor vacío requerido por JPA
    protected ProductoId() {
    }

    public ProductoId(UUID valor) {
        this.valor = Objects.requireNonNull(valor);
    }

    public static ProductoId generar() {
        return new ProductoId(UUID.randomUUID());
    }

    public UUID valor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductoId that)) return false;
        return valor.equals(that.valor);
    }

    @Override
    public int hashCode() {
        return valor.hashCode();
    }

    @Override
    public String toString() {
        return valor.toString();
    }

    public UUID getValor() {
        return valor;
    }
}