package com.uamishop.ventas.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemResponseDTO(
        UUID productoId,
        String nombreProducto,
        String sku,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {}