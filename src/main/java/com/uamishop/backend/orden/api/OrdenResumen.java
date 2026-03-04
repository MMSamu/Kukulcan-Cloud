// DTO público que representa el resumen de una orden.
// Es el único tipo que sale del módulo hacia otros módulos o hacia la capa de presentación.

package com.uamishop.backend.orden.api;

import com.uamishop.backend.orden.domain.Orden;
import com.uamishop.backend.shared.domain.Money;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrdenResumen(
        UUID ordenId,
        UUID clienteId,
        String estado,
        Money subtotal,
        Money descuento,
        Money total,
        LocalDateTime fechaCreacion) {

    /** Factory: construye un OrdenResumen a partir de la entidad de dominio. */
    public static OrdenResumen desde(Orden orden) {
        return new OrdenResumen(
                orden.getId().valor(),
                orden.getClienteId(),
                orden.getEstado().name(),
                orden.calcularSubtotal(),
                orden.getDescuento(),
                orden.getTotal(),
                orden.getFechaCreacion());
    }
}
