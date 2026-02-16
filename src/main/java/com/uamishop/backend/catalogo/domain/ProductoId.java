package com.uamishop.backend.catalogo.domain;

import java.util.UUID;

public class ProductoId {

    private final UUID valor;

    public ProductoId(UUID valor) {
        this.valor = valor;
    }

    public static ProductoId generar() {
        return null;
    }

    public UUID valor() {
        return valor;
    }
}
//public record ProductoId(UUID valor) {




    /**public ProductoId {
        if (valor == null) {
            throw new IllegalArgumentException("El id de producto no puede ser nulo");
        }
    }

    public static ProductoId generar() {
        return new ProductoId(UUID.randomUUID());
    }
}*/