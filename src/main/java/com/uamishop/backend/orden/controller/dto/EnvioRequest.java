package com.uamishop.backend.orden.controller.dto;

import jakarta.validation.constraints.*;

public record EnvioRequest(
        @NotNull(message = "La dirección de envío es obligatoria")

        DireccionEnvioRequest direccionEnvio,

        @NotBlank(message = "El número de guía es obligatorio")

        @Pattern(regexp = "^[A-Z0-9]{8,12}$", message = "El número de guía debe tener entre 8 y 12 caracteres alfanuméricos") String numeroGuia) {
}
