package com.uamishop.backend.ventas.domain;

public record Cantidad(int valor) {
    public Cantidad {
        if (valor <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        if (valor > 10) {
            throw new IllegalArgumentException("Maximo 10 unidades permitidas por producto");
        }
    }

    public Cantidad sumar(Cantidad otra) {
        return new Cantidad(this.valor + otra.valor);
    }
}