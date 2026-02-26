// DTO para representar el resumen de una orden
// Se pasan los datos necesarios para mostrar el resumen de una orden

package com.uamishop.backend.orden.api;

import com.uamishop.backend.shared.domain.Money;
import java.util.UUID;

public record OrdenResumen(
                UUID ordenId,
                UUID clienteId,
                String estado,
                Money subtotal,
                Money descuento,
                Money impuesto,
                Money total) {
}
