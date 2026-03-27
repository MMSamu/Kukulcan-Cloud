package com.uamishop.backend.orden.domain;

/**
 * Estados posibles de una orden según el diagrama de transición de estados.
 */
public enum EstadoOrden {
    PENDIENTE, // Orden creada, esperando confirmación
    CONFIRMADA, // Orden confirmada por el cliente
    PREPARACION, // Pago procesado, orden en preparación
    ENVIADA, // Orden enviada al cliente
    ENTREGADA, // Orden entregada al cliente
    CANCELADA; // Orden cancelada

    /**
     * Valida si es posible transicionar desde el estado actual al nuevo estado.
     * 
     * @param nuevo El nuevo estado al que se quiere transicionar
     * @return true si la transición es válida
     */
    public boolean puedeTransicionarA(EstadoOrden nuevo) {
        return switch (this) {
            case PENDIENTE -> nuevo == CONFIRMADA || nuevo == CANCELADA;
            case CONFIRMADA -> nuevo == PREPARACION || nuevo == CANCELADA;
            case PREPARACION -> nuevo == ENVIADA || nuevo == CANCELADA;
            case ENVIADA -> nuevo == ENTREGADA;
            case ENTREGADA -> false; // No se puede cambiar desde ENTREGADA
            case CANCELADA -> false; // No se puede cambiar desde CANCELADA
        };
    }
}