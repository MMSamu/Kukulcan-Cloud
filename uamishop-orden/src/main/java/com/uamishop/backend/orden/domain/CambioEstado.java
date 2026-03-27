package com.uamishop.backend.orden.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object que representa un cambio de estado en el historial de una orden.
 * RN-ORD-06: Se debe registrar cada cambio de estado en el historial.
 */
@Embeddable
public class CambioEstado {
    private LocalDateTime fechaCambio;

    @Enumerated(EnumType.STRING)
    private EstadoOrden estadoAnterior;

    @Enumerated(EnumType.STRING)
    private EstadoOrden estadoNuevo;

    private String motivo;

    // Constructor sin argumentos requerido por JPA
    protected CambioEstado() {
    }

    private CambioEstado(EstadoOrden estadoAnterior, EstadoOrden estadoNuevo, String motivo) {
        this.fechaCambio = LocalDateTime.now();
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.motivo = motivo;
    }

    public static CambioEstado registrar(EstadoOrden estadoAnterior, EstadoOrden estadoNuevo, String motivo) {
        return new CambioEstado(estadoAnterior, estadoNuevo, motivo);
    }

    public static CambioEstado registrar(EstadoOrden estadoAnterior, EstadoOrden estadoNuevo) {
        return new CambioEstado(estadoAnterior, estadoNuevo, null);
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public EstadoOrden getEstadoAnterior() {
        return estadoAnterior;
    }

    public EstadoOrden getEstadoNuevo() {
        return estadoNuevo;
    }

    public String getMotivo() {
        return motivo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CambioEstado that = (CambioEstado) o;
        return Objects.equals(fechaCambio, that.fechaCambio) &&
                estadoAnterior == that.estadoAnterior &&
                estadoNuevo == that.estadoNuevo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fechaCambio, estadoAnterior, estadoNuevo);
    }

    @Override
    public String toString() {
        return String.format("%s -> %s (%s)%s",
                estadoAnterior,
                estadoNuevo,
                fechaCambio,
                motivo != null ? " - " + motivo : "");
    }
}
