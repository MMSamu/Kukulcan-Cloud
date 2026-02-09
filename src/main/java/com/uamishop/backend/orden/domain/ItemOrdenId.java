package com.uamishop.backend.orden.domain;

import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object que representa el identificador único de un Item de Orden.
 * Proporciona seguridad de tipos y encapsula la lógica del ID.
 */
@Embeddable
public class ItemOrdenId {
    private UUID valor;

    // Constructor sin argumentos requerido por JPA
    protected ItemOrdenId() {
    }

    private ItemOrdenId(UUID valor) {
        this.valor = Objects.requireNonNull(valor, "El ID del item de orden no puede ser nulo");
    }

    public static ItemOrdenId generar() {
        return new ItemOrdenId(UUID.randomUUID());
    }

    public static ItemOrdenId de(UUID uuid) {
        return new ItemOrdenId(uuid);
    }

    public static ItemOrdenId de(String uuid) {
        return new ItemOrdenId(UUID.fromString(uuid));
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
        ItemOrdenId that = (ItemOrdenId) o;
        return Objects.equals(valor, that.valor);
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
