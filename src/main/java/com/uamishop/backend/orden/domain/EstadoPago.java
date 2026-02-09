package com.uamishop.backend.orden.domain;

/**
 * Estados posibles del pago de una orden.
 */
public enum EstadoPago {
    PENDIENTE, // Pago pendiente
    PROCESANDO, // Pago en proceso
    COMPLETADO, // Pago completado exitosamente
    FALLIDO, // Pago fallido
    REEMBOLSADO // Pago reembolsado
}
