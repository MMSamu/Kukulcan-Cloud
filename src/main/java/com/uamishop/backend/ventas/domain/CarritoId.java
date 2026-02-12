package com.uamishop.backend.ventas.domain;

import java.util.UUID;

public record CarritoId(UUID value) {
    public CarritoId {
        if (value == null) {
            throw new IllegalArgumentException("El ID del carrito no puede ser nulo");
        }
    }

    public static CarritoId generar() {
        return new CarritoId(UUID.randomUUID());
    }
}
