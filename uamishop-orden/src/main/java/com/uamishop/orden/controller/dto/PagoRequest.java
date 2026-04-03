package com.uamishop.orden.controller.dto;

import jakarta.validation.constraints.*;

public record PagoRequest(
        @NotBlank(message = "La referencia de pago es obligatoria")

        @Pattern(regexp = "^[A-Z0-9]{8,12}$", message = "La referencia de pago debe tener entre 8 y 12 caracteres alfanuméricos")

        String referenciaPago) {
}
