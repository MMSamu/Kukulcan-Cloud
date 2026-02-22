package com.uamishop.backend.orden.controller.dto;

import com.uamishop.backend.orden.domain.ItemOrden;
import java.util.UUID;

public record ItemOrdenResponseDTO(
        UUID productoId,
        String nombreProducto,
        String sku,
        int cantidad,
        double precioUnitario,
        double subtotal) {

    public static ItemOrdenResponseDTO fromDomain(ItemOrden item) {
        return new ItemOrdenResponseDTO(
                item.getProductoId(),
                item.getNombreProducto(),
                item.getSku(),
                item.getCantidad(),
                item.getPrecioUnitario().getCantidad().doubleValue(),
                item.calcularSubtotal().getCantidad().doubleValue());
    }
}
