package com.uamishop.backend.orden.controller.dto;

import jakarta.validation.constraints.*;

public record DireccionEnvioRequest(
        @NotBlank(message = "La calle es obligatoria") String calle,

        @NotBlank(message = "El número exterior es obligatorio") String numeroExterior,

        @NotBlank(message = "El código postal es obligatorio") @Pattern(regexp = "\\d{5}", message = "El código postal debe tener 5 dígitos") String codigoPostal,

        @NotBlank(message = "La ciudad es obligatoria") String ciudad,

        @NotBlank(message = "El estado es obligatorio") String estado) {
}
