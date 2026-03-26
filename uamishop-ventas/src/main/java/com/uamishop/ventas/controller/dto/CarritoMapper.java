package com.uamishop.ventas.controller.dto;

import com.uamishop.ventas.domain.Carrito;

public class CarritoMapper {

    private CarritoMapper() {
    }

    public static CarritoResponseDTO toDTO(Carrito carrito) {
        var items = carrito.getItems().stream()
                .map(i -> new ItemResponseDTO(
                        i.getProductoId().valor(),
                        i.getNombreProducto(),
                        i.getSku(),
                        i.getCantidad(),
                        i.getPrecioUnitario().getCantidad(),
                        i.subtotal().getCantidad()
                )).toList();

        return new CarritoResponseDTO(
                carrito.getId().value(),
                carrito.getClienteId().getValor(),
                items,
                carrito.calcularTotal().sumar(carrito.getDescuento()).getCantidad(),
                carrito.getDescuento().getCantidad(),
                carrito.calcularTotal().getCantidad(),
                carrito.getEstado().name()
        );
    }
}