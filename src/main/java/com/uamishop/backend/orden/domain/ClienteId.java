package com.uamishop.backend.orden.domain;

import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;

/**
 * Value Object que representa el identificador único de un Cliente.
 * Proporciona seguridad de tipos y encapsula la lógica del ID.
 */
@Embeddable
public class ClienteId {
    private UUID valor;

    // Constructor sin argumentos requerido por JPA
    protected ClienteId() {
    }

    private ClienteId(UUID valor) {
        this.valor = Objects.requireNonNull(valor, "El ID del cliente no puede ser nulo");
    }

    public static ClienteId generar() {
        return new ClienteId(UUID.randomUUID());
    }

    public static ClienteId de(UUID uuid) {
        return new ClienteId(uuid);
    }

    public static ClienteId de(String uuid) {
        return new ClienteId(UUID.fromString(uuid));
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
        ClienteId clienteId = (ClienteId) o;
        return Objects.equals(valor, clienteId.valor);
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
