package com.uamishop.orden.controller.dto;

public record EnvioResponseDTO(
        String direccionEnvio,
        String numeroGuia,
        String estadoEnvio) {
}
