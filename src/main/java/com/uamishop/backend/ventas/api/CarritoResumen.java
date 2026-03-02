package com.uamishop.backend.ventas.api;

import com.uamishop.backend.shared.domain.ClienteId;
import com.uamishop.backend.shared.domain.ProductoId;
import com.uamishop.backend.shared.domain.Money;
import java.util.List;
import java.util.UUID;

// DTO para exponer el resumen del carrito
public record CarritoResumen(
    UUID carritoId,
    ClienteId clienteId,
    String estado,
    List<ItemCarritoResumen> items
) {
    // DTO interno para los productos del carrito
    public record ItemCarritoResumen(
        ProductoId productoId,
        String nombreProducto,
        String sku,
        int cantidad,
        Money precioUnitario
    ) {}
}

