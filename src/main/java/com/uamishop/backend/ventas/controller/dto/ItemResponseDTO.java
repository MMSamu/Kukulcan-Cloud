package com.uamishop.backend.ventas.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

/* Este Record actúa como un "Contenedor de Datos de Salida".
 * Se usa cuando el Backend envía información sobre un item en el carrito.
 */
public record ItemResponseDTO(
        UUID productoId,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {}