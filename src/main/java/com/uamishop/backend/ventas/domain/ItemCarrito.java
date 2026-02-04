package com.uamishop.backend.ventas.domain;

import com.uamishop.backend.shared.domain.Money;
import java.util.UUID;

public class ItemCarrito {
    private UUID id;
    private UUID productoId;
    private int cantidad;
    private Money precioUnitario;

    public ItemCarrito(UUID productoId, int cantidad, Money precioUnitario) {
        this.id = UUID.randomUUID();
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public void aumentarCantidad(int cantidadExtra) {
        this.cantidad += cantidadExtra;
    }

    // Calcula cuanto es (Precio x Cantidad)
    public Money subtotal() {
        return precioUnitario.multiplicar(cantidad);
    }

    public UUID getProductoId() { return productoId; }
    public int getCantidad() { return cantidad; }
}