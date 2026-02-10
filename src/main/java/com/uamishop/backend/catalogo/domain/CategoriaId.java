package com.uamishop.backend.catalogo.domain;

import java.util.UUID;

public record CategoriaId(UUID valor) {

    public CategoriaId {
        if (valor == null) {
            throw new IllegalArgumentException("El id de categoría no puede ser nulo");
        }
    }

    public static CategoriaId generar() {
        return new CategoriaId(UUID.randomUUID());
    }
}
