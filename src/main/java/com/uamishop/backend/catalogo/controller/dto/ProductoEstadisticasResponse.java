package com.uamishop.backend.catalogo.controller.dto;

import java.time.LocalDateTime;

public record ProductoEstadisticasResponse(
                Long ventasTotales,
                Long cantidadVendida,
                Long vecesAgregadoAlCarrito,
                LocalDateTime ultimaVenta) {
}
