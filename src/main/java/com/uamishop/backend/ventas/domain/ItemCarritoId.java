package com.uamishop.backend.ventas.domain;

import java.util.UUID;

public record ItemCarritoId(UUID value) {
    public ItemCarritoId {
        if (value == null) {
            throw new IllegalArgumentException("El ID del item no puede ser nulo");
        }
    }

    public static ItemCarritoId generar() {
        return new ItemCarritoId(UUID.randomUUID());
    }
}
