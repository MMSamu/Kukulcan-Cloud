package com.uamishop.backend.shared.domain;

//import com.uamishop.backend.catalogo.domain.Producto;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object para manejar dinero de forma segura.
 * Evita problemas de precisión que ocurren con 'double'.
 */
@Embeddable
public class Money {
    private final BigDecimal cantidad;
    private final String moneda;


    //Constructor para JPA (protegido para que nadie más lo use)
    protected Money() {
        this.cantidad = null;
        this.moneda = null;
    }
    // Constructor privado
    public Money(BigDecimal cantidad, String moneda) {

        if (cantidad == null) {
            throw new IllegalArgumentException("La cantidad no puede ser null");
        }

        if (moneda == null || moneda.isBlank()) {
            throw new IllegalArgumentException("La moneda no puede ser null o vacía");
        }

        // RN-VO-02: No se permiten saldos negativos
        if (cantidad.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("No se permiten cantidades negativas: " + cantidad);
        }
        this.cantidad = cantidad;
        this.moneda = moneda;
    }

    /**
     * Fábrica estática genérica.
     */
    public static Money of(BigDecimal cantidad, String moneda) {
        return new Money(cantidad, moneda);
    }

    // Fabrica estática para crear pesos
    public static Money pesos(BigDecimal cantidad) {
        return new Money(cantidad, "MXN");
    }

    // Metodo para sumar dinero (valida que sea la misma moneda)
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

    private void validarMoneda(Money otro) {
        if (!this.moneda.equals(otro.moneda)) {
            throw new IllegalArgumentException("Monedas distintas: " + this.moneda + " vs " + otro.moneda);
        }
    }

    // Métodos de comparación para reglas de negocio
    public boolean esPositivo() {
        return this.cantidad.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean esMayorQue(Money otro) {
        validarMoneda(otro);
        return this.cantidad.compareTo(otro.cantidad) > 0;
    }

    public BigDecimal getCantidad() {
        //producto.getPrecio().getCantidad();
        return cantidad;
    }

    public String getMoneda() {
        return moneda;
    }

    /**
     * equals y hashCode obligatorios en Value Objects
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return cantidad.equals(money.cantidad) &&
                moneda.equals(money.moneda);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cantidad, moneda);
    }

    @Override
    public String toString() {

        return cantidad + " " + moneda;
    }

    /**public BigDecimal getAmount() {

        return null;
    }*/
}