package com.uamishop.orden.controller.dto;

public record DireccionEnvioResponseDTO(
        String calle,
        String numeroExterior,
        String codigoPostal,
        String ciudad,
        String estado) {
}
