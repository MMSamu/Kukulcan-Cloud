package com.uamishop.backend.orden.domain;

public enum EstadoOrden {
    PENDIENTE, // Orden creada
    CONFIRMADA, // Orden confirmada
    PAGOENPROCESO, // Pago en proceso
    ENPREPARACION, // En preparacion
    ENVIADA, // Enviada al cliente
    EN_TRANSITO, // En transito
    ENTREGADA, // Entregada
    CANCELADA // Cancelada
}