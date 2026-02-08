package com.uamishop.backend.shared.domain;

import java.math.BigDecimal;

public class Money {
    private final BigDecimal cantidad;
    private final String moneda;

    private Money(BigDecimal cantidad, String moneda) {
        this.cantidad = cantidad;
        this.moneda = moneda;
    }

    public static Money pesos(double cantidad) {
        return new Money(BigDecimal.valueOf(cantidad), "MXN");
    }

    public Money sumar(Money otro) {
        validarMoneda(otro);
        return new Money(this.cantidad.add(otro.cantidad), this.moneda);
    }

    public Money multiplicar(int factor) {
        return new Money(this.cantidad.multiply(BigDecimal.valueOf(factor)), this.moneda);
    }

    public Money restar(Money otro) {
        validarMoneda(otro);
        return new Money(this.cantidad.subtract(otro.cantidad), this.moneda);
    }

    public boolean esMayorQue(Money otro) {
        validarMoneda(otro);
        return this.cantidad.compareTo(otro.cantidad) > 0;
    }

    private void validarMoneda(Money otro) {
        if (!this.moneda.equals(otro.moneda)) {
            throw new IllegalArgumentException("Monedas distintas: " + this.moneda + " vs " + otro.moneda);
        }
    }

    public BigDecimal getCantidad() { return cantidad; }
    public String getMoneda() { return moneda; }
}