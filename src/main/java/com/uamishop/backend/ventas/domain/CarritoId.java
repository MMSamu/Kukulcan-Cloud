package com.uamishop.backend.ventas.domain;

import java.util.UUID;

/* Representa el ID de un carrito de compras */
public record CarritoId(UUID value) {
    // Validación para asegurar que el ID no sea nulo
    public CarritoId {
        if (value == null) {
            throw new IllegalArgumentException("El ID del carrito no puede ser nulo");
        }
    }
    // Método estático para generar un nuevo ID de carrito
    public static CarritoId generar() {
        return new CarritoId(UUID.randomUUID());
    }
}
