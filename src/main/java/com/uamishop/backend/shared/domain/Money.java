package com.uamishop.backend.shared.domain;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

<<<<<<< HEAD
/**
 * Value Object para manejar dinero de forma segura.
 * Evita problemas de precisión que ocurren con 'double'.
 */
@Embeddable
public class Money {
    private final BigDecimal cantidad;
    private final String moneda;

    // Constructor privado
    private Money(BigDecimal cantidad, String moneda) {
        // RN-VO-02: No se permiten saldos negativos
        if (cantidad.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("No se permiten cantidades negativas: " + cantidad);
        }
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
            throw new IllegalArgumentException(
                    "No se pueden sumar monedas distintas: " + this.moneda + " vs " + otro.moneda);
        }
        return new Money(this.cantidad.add(otro.cantidad), this.moneda);
    }

    public Money multiplicar(int factor) {
        return new Money(this.cantidad.multiply(BigDecimal.valueOf(factor)), this.moneda);
    }

    // Métodos de comparación para reglas de negocio
    public boolean esPositivo() {
        return this.cantidad.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean esMayorQue(Money otro) {
        if (!this.moneda.equals(otro.moneda)) {
            throw new IllegalArgumentException("No se pueden comparar monedas distintas");
        }
        return this.cantidad.compareTo(otro.cantidad) > 0;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public String getMoneda() {
        return moneda;
    }
}