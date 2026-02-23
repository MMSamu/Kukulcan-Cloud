package com.uamishop.backend.orden.controller.dto;

import com.uamishop.backend.orden.domain.Orden;
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

    public static OrdenResponseDTO fromDomain(Orden orden) {
        return new OrdenResponseDTO(
                orden.getId().valor(),
                orden.getClienteId(),
                orden.getItems().stream().map(ItemOrdenResponseDTO::fromDomain).toList(),
                orden.getEstado().name(),
                orden.getDireccionEnvio() != null ? orden.getDireccionEnvio().toString() : "",
                orden.getTotal().getCantidad().doubleValue(),
                orden.getFechaCreacion(),
                LocalDateTime.now() // Placeholder if update date is not available
        );
    }
}
