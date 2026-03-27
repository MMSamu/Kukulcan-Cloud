package com.uamishop.backend.orden.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object para representar la información de envío de una orden.
 * Es inmutable.
 */
@Embeddable
public class InfoEnvio implements Serializable {

    private String proveedorLogistico;
    private String numeroGuia;
    private LocalDateTime fechaEstimadaEntrega;

    // Constructor vacío requerido por JPA
    protected InfoEnvio() {
    }

    public InfoEnvio(String proveedorLogistico, String numeroGuia, LocalDateTime fechaEstimadaEntrega) {
        this.proveedorLogistico = proveedorLogistico;
        this.numeroGuia = numeroGuia;
        this.fechaEstimadaEntrega = fechaEstimadaEntrega;
    }

    // Factory method simple
    public static InfoEnvio of(String proveedorLogistico, String numeroGuia, LocalDateTime fechaEstimadaEntrega) {
        return new InfoEnvio(proveedorLogistico, numeroGuia, fechaEstimadaEntrega);
    }

    public String getProveedorLogistico() {
        return proveedorLogistico;
    }

    public String getNumeroGuia() {
        return numeroGuia;
    }

    public LocalDateTime getFechaEstimadaEntrega() {
        return fechaEstimadaEntrega;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        InfoEnvio infoEnvio = (InfoEnvio) o;
        return Objects.equals(proveedorLogistico, infoEnvio.proveedorLogistico) &&
                Objects.equals(numeroGuia, infoEnvio.numeroGuia) &&
                Objects.equals(fechaEstimadaEntrega, infoEnvio.fechaEstimadaEntrega);
    }

    @Override
    public int hashCode() {
        return Objects.hash(proveedorLogistico, numeroGuia, fechaEstimadaEntrega);
    }

    @Override
    public String toString() {
        return "InfoEnvio{" +
                "proveedorLogistico='" + proveedorLogistico + '\'' +
                ", numeroGuia='" + numeroGuia + '\'' +
                ", fechaEstimadaEntrega=" + fechaEstimadaEntrega +
                '}';
    }
}
