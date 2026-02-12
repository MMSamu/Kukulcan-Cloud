package com.uamishop.backend.ventas.domain;

import com.uamishop.backend.shared.domain.Money;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "items_carrito")
public class ItemCarrito {

    @Id
    private UUID id;

    @Column(name = "producto_id")
    private UUID productoId;

    @Column(name = "cantidad")
    private int cantidad; // JPA guarda el número simple

    @Embedded
    private Money precioUnitario;

    // Constructor vacio obligatorio para JPA
    protected ItemCarrito() { }

    public ItemCarrito(UUID productoId, int cantidad, Money precioUnitario) {
        this.id = UUID.randomUUID();
        this.productoId = productoId;
        // Validamos usando Record, pero guardamos el valor primitivo
        this.cantidad = new Cantidad(cantidad).valor();
        this.precioUnitario = precioUnitario;
    }

    public void aumentarCantidad(Cantidad cantidadExtra) {
        Cantidad actual = new Cantidad(this.cantidad);
        this.cantidad = actual.sumar(cantidadExtra).valor();
    }

    public void actualizarCantidad(Cantidad nuevaCantidad) {
        this.cantidad = nuevaCantidad.valor();
    }

    public Money subtotal() {
        return precioUnitario.multiplicar(cantidad);
    }

    public UUID getProductoId() { return productoId; }
    public int getCantidad() { return cantidad; }
}