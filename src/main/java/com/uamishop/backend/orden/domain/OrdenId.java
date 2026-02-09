package com.uamishop.backend.orden.domain;

import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object que representa el identificador único de una Orden.
 * Proporciona seguridad de tipos y encapsula la lógica del ID.
 */
@Embeddable
public class OrdenId {
    private UUID valor;

    // Constructor sin argumentos requerido por JPA
    protected OrdenId() {
    }

    private OrdenId(UUID valor) {
        this.valor = Objects.requireNonNull(valor, "El ID de la orden no puede ser nulo");
    }

    public static OrdenId generar() {
        return new OrdenId(UUID.randomUUID());
    }

    public static OrdenId de(UUID uuid) {
        return new OrdenId(uuid);
    }

    public static OrdenId de(String uuid) {
        return new OrdenId(UUID.fromString(uuid));
    }

    public UUID getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        OrdenId ordenId = (OrdenId) o;
        return Objects.equals(valor, ordenId.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor.toString();
    }
}
