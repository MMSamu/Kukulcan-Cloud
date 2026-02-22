package com.uamishop.backend.orden.controller.dto;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

public record OrdenResponseDTO(
                UUID id,
                UUID clienteId,
                List<ItemOrdenResponseDTO> items,
                String estado,
                String direccionEnvio,
                double total,
                LocalDateTime fechaCreacion,
                LocalDateTime fechaActualizacion) {
}
