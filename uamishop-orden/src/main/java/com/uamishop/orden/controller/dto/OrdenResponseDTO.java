package com.uamishop.orden.controller.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrdenResponseDTO(
        UUID id,
        UUID clienteId,
        String estado,
        double subtotal,
        double descuento,
        double total,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {

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