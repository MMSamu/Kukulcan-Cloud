// DTO público que representa el resumen de una orden.
// Es el único tipo que sale del módulo hacia otros módulos o hacia la capa de presentación.

package com.uamishop.orden.controller.dto;

import com.uamishop.shared.domain.Money;
import com.uamishop.orden.domain.Orden;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrdenResumen(
    UUID ordenId,
    UUID clienteId,
    String estado,
    Money subtotal,
    Money descuento,
    Money total,
    LocalDateTime fechaCreacion
) {
    public static OrdenResumen desde(Orden orden) {
    return new OrdenResumen(
        orden.getId().valor(),       // Usamos .valor() porque getId() devuelve un OrdenId
        orden.getClienteId(),
        orden.getEstado().name(), 
        orden.calcularSubtotal(),    // <--- Cambio: El método se llama calcularSubtotal()
        orden.getDescuento(),        // Este sí existe como getDescuento()
        orden.getTotal(),            // Este sí existe como getTotal()
        orden.getFechaCreacion()     // Este sí existe como getFechaCreacion()
    );
    }
}
