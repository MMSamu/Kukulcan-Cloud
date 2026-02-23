package com.uamishop.backend.orden.domain;

import java.util.UUID;

/**
 * Value Object que representa el identificador único de una Orden.
 * Proporciona seguridad de tipos y encapsula la lógica del ID.
 */

public record OrdenId(UUID valor) {

    public OrdenId {
        if (valor == null) {
            throw new IllegalArgumentException("El ID de la orden no puede ser nulo");
        }
    }

    public static OrdenId generar() {
        return new OrdenId(UUID.randomUUID());
    }
}
