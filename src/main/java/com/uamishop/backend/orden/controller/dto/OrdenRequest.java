package com.uamishop.backend.orden.controller.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.*;

public record OrdenRequest(
        @NotNull(message = "El ID del cliente es obligatorio") UUID clienteId,

        @NotEmpty(message = "La orden debe tener al menos un ítem") List<ItemOrdenRequest> items,

        @NotNull(message = "La dirección de envío es obligatoria") DireccionEnvioRequest direccionEnvio) {
}
