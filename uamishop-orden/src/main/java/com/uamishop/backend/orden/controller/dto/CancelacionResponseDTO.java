package com.uamishop.backend.orden.controller.dto;

public record CancelacionResponseDTO(
        String estado, // Debería ser "CANCELADA"
        String mensaje) {
}
