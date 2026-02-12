package com.uamishop.backend.ventas.domain;

import com.uamishop.backend.shared.domain.Money;
import java.util.UUID;

public class ItemCarrito {
    private final ItemCarritoId id;
    private final UUID productoId;
    private Cantidad cantidad; // Ahora usa el Record
    private final Money precioUnitario;

    // Constructor: Recibe 'int' pero lo convierte internamente
    public ItemCarrito(UUID productoId, int cantidad, Money precioUnitario) {
        this.id = ItemCarritoId.generar();
        this.productoId = productoId;
        this.cantidad = new Cantidad(cantidad); // Validación automatica
        this.precioUnitario = precioUnitario;
    }

    public void aumentarCantidad(Cantidad cantidadExtra) {
        this.cantidad = this.cantidad.sumar(cantidadExtra);
    }

    public void actualizarCantidad(Cantidad nuevaCantidad) {
        this.cantidad = nuevaCantidad;
    }

    public Money subtotal() {
        return precioUnitario.multiplicar(cantidad.valor());
    }

    public UUID getProductoId() { return productoId; }

    // Getter devuelve int para facilitar compatibilidad
    public int getCantidad() { return cantidad.valor(); }
}