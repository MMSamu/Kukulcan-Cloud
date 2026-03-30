// DTO público que representa el resumen de una orden.
// Es el único tipo que sale del módulo hacia otros módulos o hacia la capa de presentación.

package com.uamishop.backend.orden.controller.dto;

import com.uamishop.backend.orden.domain.Orden;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrdenResumen(
    UUID ordenId,
    UUID clienteId,
    String estado,
    Double subtotal,
    Double descuento,
    Double total,
    LocalDateTime fechaCreacion
) {

    /** Factory: construye un OrdenResumen a partir de la entidad de dominio. */
 public static OrdenResumen desde(Orden orden) {
        return new OrdenResumen(
            orden.getId().valor(),
            orden.getClienteId(),
            orden.getEstado().name(),
            orden.getSubtotal() != null ? orden.getSubtotal().getCantidad().doubleValue() : 0.0,
            orden.getDescuento() != null ? orden.getDescuento().getCantidad().doubleValue() : 0.0,
            orden.getTotal() != null ? orden.getTotal().getCantidad().doubleValue() : 0.0,
            orden.getFechaCreacion()
        );
    }
}
