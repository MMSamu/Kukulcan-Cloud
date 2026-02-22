package com.uamishop.backend.ventas.domain;

import java.util.UUID;

/* Representa el ID de un item en el carrito de compras */
public record ItemCarritoId(UUID value) {
    // Validación para asegurar que el ID no sea nulo
    public ItemCarritoId {
        if (value == null) {
            throw new IllegalArgumentException("El ID del item no puede ser nulo");
        }
    }

    // Método estático para generar un nuevo ID de item utilizando UUID
    public static ItemCarritoId generar() {
        return new ItemCarritoId(UUID.randomUUID());
    }
}