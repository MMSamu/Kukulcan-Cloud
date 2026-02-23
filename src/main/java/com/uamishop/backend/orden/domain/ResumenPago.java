package com.uamishop.backend.orden.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object para representar el resumen de pago de una orden.
 * Es inmutable.
 */
@Embeddable
public class ResumenPago implements Serializable {

    @Column(name = "pago_metodo")
    private String metodoPago;

    @Column(name = "pago_referencia")
    private String referenciaExterna;

    @Enumerated(EnumType.STRING)
    @Column(name = "pago_estado")
    private EstadoPago estado;

    @Column(name = "pago_fecha")
    private LocalDateTime fechaProcesamiento;

    // Constructor vacío requerido por JPA
    protected ResumenPago() {
    }

    public ResumenPago(String metodoPago, String referenciaExterna, EstadoPago estado,
            LocalDateTime fechaProcesamiento) {
        this.metodoPago = metodoPago;
        this.referenciaExterna = referenciaExterna;
        this.estado = estado;
        this.fechaProcesamiento = fechaProcesamiento;
    }

    // Factory method para crear un pago pendiente
    public static ResumenPago pendiente() {
        return new ResumenPago(null, null, EstadoPago.PENDIENTE, null);
    }

    // Factory method para crear un pago completado
    public static ResumenPago completado(String metodoPago, String referenciaExterna,
            LocalDateTime fechaProcesamiento) {
        return new ResumenPago(metodoPago, referenciaExterna, EstadoPago.COMPLETADO, fechaProcesamiento);
    }

    // Getters
    public String getMetodoPago() {
        return metodoPago;
    }

    public String getReferenciaExterna() {
        return referenciaExterna;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public LocalDateTime getFechaProcesamiento() {
        return fechaProcesamiento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ResumenPago that = (ResumenPago) o;
        return Objects.equals(metodoPago, that.metodoPago) &&
                Objects.equals(referenciaExterna, that.referenciaExterna) &&
                estado == that.estado &&
                Objects.equals(fechaProcesamiento, that.fechaProcesamiento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metodoPago, referenciaExterna, estado, fechaProcesamiento);
    }

    @Override
    public String toString() {
        return "ResumenPago{" +
                "metodoPago='" + metodoPago + '\'' +
                ", referenciaExterna='" + referenciaExterna + '\'' +
                ", estado=" + estado +
                ", fechaProcesamiento=" + fechaProcesamiento +
                '}';
    }
}
