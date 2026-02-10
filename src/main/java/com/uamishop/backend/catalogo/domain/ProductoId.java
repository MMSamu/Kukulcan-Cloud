package com.uamishop.backend.catalogo.domain;

import java.util.UUID;

public record ProductoId(UUID valor) {

    public ProductoId {
        if (valor == null) {
            throw new IllegalArgumentException("El id de producto no puede ser nulo");
        }
    }

    public static ProductoId generar() {
        return new ProductoId(UUID.randomUUID());
    }
}