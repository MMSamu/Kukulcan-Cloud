package com.uamishop.backend.orden.controller.dto;

import java.util.UUID;

public record ItemOrdenResponseDTO(
                UUID productoId,
                int cantidad,
                double precioUnitario,
                double subtotal) {
}
