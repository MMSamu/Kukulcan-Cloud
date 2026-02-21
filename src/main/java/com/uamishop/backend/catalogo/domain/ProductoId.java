package com.uamishop.backend.catalogo.domain;

import java.util.Objects;
import java.util.UUID;

public class ProductoId {

    private final UUID valor;

    public ProductoId(UUID valor) {
        this.valor = Objects.requireNonNull(valor);
    }

    public static ProductoId generar() {
        return new ProductoId(UUID.randomUUID());
    }

    public UUID valor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductoId that)) return false;
        return valor.equals(that.valor);
    }

    @Override
    public int hashCode() {
        return valor.hashCode();
    }

    @Override
    public String toString() {
        return valor.toString();
    }

}
