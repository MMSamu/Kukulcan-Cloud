package com.uamishop.backend.shared.domain;

import java.math.BigDecimal;

/**
 * Value Object para manejar dinero de forma segura.
 * Evita problemas de precisión que ocurren con 'double'.
 */
public class Money {
    private final BigDecimal cantidad;
    private final String moneda;

    // Constructor privado
    private Money(BigDecimal cantidad, String moneda) {
        this.cantidad = cantidad;
        this.moneda = moneda;
    }

    // Fabrica estática para crear pesos
    public static Money pesos(double cantidad) {
        return new Money(BigDecimal.valueOf(cantidad), "MXN");
    }

    // Metodo para sumar dinero (valida que sea la misma moneda)
    public Money sumar(Money otro) {
        if (!this.moneda.equals(otro.moneda)) {
            throw new IllegalArgumentException("No se pueden sumar monedas distintas: " + this.moneda + " vs " + otro.moneda);
        }
        return new Money(this.cantidad.add(otro.cantidad), this.moneda);
    }

    public Money multiplicar(int factor) {
        return new Money(this.cantidad.multiply(BigDecimal.valueOf(factor)), this.moneda);
    }

    public BigDecimal getCantidad() { return cantidad; }
    public String getMoneda() { return moneda; }
}