package com.uamishop.backend.ventas.controller.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/* Este Record actúa como un "Contenedor de Datos de Salida".
 * Se usa cuando el Backend envía información sobre un carrito.
 */
public record CarritoResponseDTO(
    UUID id,
    UUID clienteId,
    List<ItemResponseDTO> items,
    BigDecimal subtotal,
    BigDecimal descuento,
    BigDecimal total,
    String estado
) {}