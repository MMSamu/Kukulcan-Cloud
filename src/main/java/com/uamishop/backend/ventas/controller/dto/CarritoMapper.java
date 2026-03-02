package com.uamishop.backend.ventas.controller.dto;

import com.uamishop.backend.ventas.domain.Carrito;

public class CarritoMapper {

    // Clase para mapear el dominio Carrito a CarritoResponseDTO
    private CarritoMapper() {
    }
    
    public static CarritoResponseDTO toDTO(Carrito carrito) {
        // Extraemos UUID y BigDecimal de los Value Objects
        var items = carrito.getItems().stream()
                .map(i -> new ItemResponseDTO(
                        i.getProductoId().valor(), //Devuelve el valor
                        i.getCantidad(),
                        i.getPrecioUnitario().getCantidad(), // Precio
                        i.subtotal().getCantidad()           // Subtotal
                )).toList();

        // Construcción del DTO
        return new CarritoResponseDTO(
                carrito.getId().value(),              // UUID del carrito
                carrito.getClienteId().getValor(),    // UUID del cliente
                items,
                carrito.calcularTotal().sumar(carrito.getDescuento()).getCantidad(), // Subtotal
                carrito.getDescuento().getCantidad(), // Dscuento
                carrito.calcularTotal().getCantidad(),// Total
                carrito.getEstado().name()            // String del Estado del carrito
        );
    }
}