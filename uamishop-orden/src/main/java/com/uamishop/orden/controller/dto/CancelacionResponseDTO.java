package com.uamishop.orden.controller.dto;

public record CancelacionResponseDTO(
        String estado, // Debería ser "CANCELADA"
        String mensaje) {
}
