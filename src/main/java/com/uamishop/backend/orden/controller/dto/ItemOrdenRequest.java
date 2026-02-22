package com.uamishop.backend.orden.controller.dto;

import java.util.UUID;

import jakarta.validation.constraints.*;

public record ItemOrdenRequest(
        @NotNull(message = "El ID del producto es obligatorio") UUID productoId,

        @Positive(message = "La cantidad debe ser mayor a cero") int cantidad) {
}
