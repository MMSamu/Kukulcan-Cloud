package com.uamishop.catalogo.controller.dto;

import java.time.LocalDateTime;

public record ProductoEstadisticasResponse(
                Long ventasTotales,
                Long cantidadVendida,
                Long vecesAgregadoAlCarrito,
                LocalDateTime ultimaVenta) {
}
