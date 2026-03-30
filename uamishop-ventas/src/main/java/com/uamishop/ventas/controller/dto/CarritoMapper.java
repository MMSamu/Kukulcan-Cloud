package com.uamishop.ventas.controller.dto;

import com.uamishop.ventas.domain.Carrito;
import java.math.BigDecimal;
import java.util.List;

public class CarritoMapper {

    private CarritoMapper() {
    }

    public static CarritoResponseDTO toDTO(Carrito carrito) {
        // 1. Mapeamos los items. 
        // Como 'i.getCantidad()' ya devuelve un int, no necesitas llamar a .getValor()
        List<ItemResponseDTO> items = carrito.getItems().stream()
                .map(i -> new ItemResponseDTO(
                        i.getProductoId().valor(),
                        i.getNombreProducto() != null ? i.getNombreProducto() : "Sin nombre",
                        i.getSku() != null ? i.getSku() : "S/N",
                        i.getCantidad(), 
                        i.getPrecioUnitario().getCantidad(),
                        i.subtotal().getCantidad()
                )).toList();

        // 2. Calculamos los montos de forma segura
        // El descuento nunca será null porque tu constructor de Carrito pone Money.pesos(0)
        BigDecimal descuentoMonto = carrito.getDescuento().getCantidad();
        BigDecimal totalMonto = carrito.calcularTotal().getCantidad();
        
        // El subtotal es la suma del total + el descuento
        BigDecimal subtotalMonto = totalMonto.add(descuentoMonto);

        return new CarritoResponseDTO(
                carrito.getId().value(),
                carrito.getClienteId().getValor(),
                items,
                subtotalMonto,
                descuentoMonto,
                totalMonto,
                carrito.getEstado().name()
        );
    }
}