package com.uamishop.backend.ventas.controller.dto;

import com.uamishop.backend.ventas.domain.Carrito;

/*Este mapper convierte un Carrito de dominio a un CarritoResponseDTO para la capa de presentación*/
public class CarritoMapper {
    // Convierte un Carrito a CarritoResponseDTO
    public static CarritoResponseDTO toDTO(Carrito carrito) {
        // Para cada item en el carrito, se crea un ItemResponseDTO con el productoId,
        // cantidad, precio unitario y subtotal
        var items = carrito.getItems().stream()
                .map(i -> new ItemResponseDTO(
                        i.getProductoId(),
                        i.getCantidad(),
                        i.subtotal().multiplicar(1).getCantidad().divide(new java.math.BigDecimal(i.getCantidad())), // precio unitario
                        i.subtotal().getCantidad()
                )).toList();

        /* Se construye el CarritoResponseDTO con toda la información relevante del carrito,
        incluyendo el subtotal, descuento, total y estado */
        return new CarritoResponseDTO(
                carrito.getId().value(),
                carrito.getClienteId(),
                items,
                carrito.calcularTotal().sumar(carrito.getDescuento()).getCantidad(), // subtotal
                carrito.getDescuento().getCantidad(),
                carrito.calcularTotal().getCantidad(),
                carrito.getEstado().name()
        );
    }
}