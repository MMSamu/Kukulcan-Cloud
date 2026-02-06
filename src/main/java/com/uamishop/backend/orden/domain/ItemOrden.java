package com.uamishop.backend.orden.domain;

import com.uamishop.backend.shared.domain.Money;
import java.util.UUID;

public class ItemOrden {
    private UUID id;
    private UUID productoId;
    private String nombreProducto;
    private String sku;
    private int cantidad;
    private Money precioUnitario;

    public void calcularSubtotal(UUID productoId, String nombreProducto, String sku, int cantidad,
            Money precioUnitario) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.sku = sku;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Money subtotal() {
        return precioUnitario.multiplicar(cantidad);
    }

    public UUID getProductoId() {
        return productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public String getSku() {
        return sku;
    }

    public int getCantidad() {
        return cantidad;
    }

    public Money getPrecioUnitario() {
        return precioUnitario;
    }
}