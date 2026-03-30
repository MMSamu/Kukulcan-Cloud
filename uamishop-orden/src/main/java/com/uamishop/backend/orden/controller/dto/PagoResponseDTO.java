package com.uamishop.backend.orden.controller.dto;

public record PagoResponseDTO(
        String referenciaPago,
        String estado,
        double monto,
        String metodoPago) {
}
