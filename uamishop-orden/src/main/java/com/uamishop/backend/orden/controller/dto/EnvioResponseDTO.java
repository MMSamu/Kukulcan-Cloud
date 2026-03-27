package com.uamishop.backend.orden.controller.dto;

public record EnvioResponseDTO(
        String direccionEnvio,
        String numeroGuia,
        String estadoEnvio) {
}
