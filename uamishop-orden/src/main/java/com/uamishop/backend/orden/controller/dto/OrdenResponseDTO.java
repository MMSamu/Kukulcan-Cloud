package com.uamishop.backend.orden.controller.dto;

import com.uamishop.backend.orden.api.OrdenResumen;
import java.util.UUID;
import java.time.LocalDateTime;

public record OrdenResponseDTO(
        UUID id,
        UUID clienteId,
        String estado,
        double subtotal,
        double descuento,
        double total,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {

    /** Construye el DTO de respuesta desde el OrdenResumen público del módulo. */
    public static OrdenResponseDTO fromResumen(OrdenResumen resumen) {
        return new OrdenResponseDTO(
                resumen.ordenId(),
                resumen.clienteId(),
                resumen.estado(),
                resumen.subtotal() != null ? resumen.subtotal().getCantidad().doubleValue() : 0.0,
                resumen.descuento() != null ? resumen.descuento().getCantidad().doubleValue() : 0.0,
                resumen.total() != null ? resumen.total().getCantidad().doubleValue() : 0.0,
                resumen.fechaCreacion(),
                LocalDateTime.now());
    }
}
