package com.uamishop.backend.ventas.domain;

/* Representa una cantidad de un producto en el carrito */
public record Cantidad(int valor) {
    // Validaciones para asegurar que la cantidad sea positiva y no excesiva
    public Cantidad {
        if (valor <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        if (valor > 10) {
            throw new IllegalArgumentException("Maximo 10 unidades permitidas por producto");
        }
    }

    // Método para sumar cantidades, útil para agregar más unidades de un producto ya existente en el carrito
    public Cantidad sumar(Cantidad otra) {
        return new Cantidad(this.valor + otra.valor);
    }
}