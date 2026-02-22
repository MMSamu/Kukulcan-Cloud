package com.uamishop.backend.ventas.domain;

import com.uamishop.backend.shared.domain.Money;
import jakarta.persistence.*;
import java.util.UUID;

/* Representa un item en el carrito de compras */
@Entity
@Table(name = "items_carrito")
public class ItemCarrito {

    // Identificador único del item (no del producto, sino de la línea en el
    // carrito)
    @Id
    private UUID id;

    // Referencia al producto (solo su ID, no toda la entidad)
    @Column(name = "producto_id")
    private UUID productoId;

    // JPA guarda el número simple
    @Column(name = "cantidad_items")
    private int cantidad;

    // Nombre del producto
    @Column(name = "nombre_producto")
    private String nombreProducto;

    // SKU del producto
    @Column(name = "sku")
    private String sku;

    // Precio unitario del producto
    @Embedded
    private Money precioUnitario;

    // Constructor vacio obligatorio para JPA
    protected ItemCarrito() {
    }

    // Constructor para crear un nuevo item en el carrito
    public ItemCarrito(UUID productoId, String nombreProducto, String sku, int cantidad, Money precioUnitario) {
        this.id = UUID.randomUUID();
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.sku = sku;
        // Validamos usando Record, pero guardamos el valor primitivo
        this.cantidad = new Cantidad(cantidad).valor();
        this.precioUnitario = precioUnitario;
    }

    // Permite aumentar la cantidad sumando a la existente (ej: agregar más del
    // mismo producto)
    public void aumentarCantidad(Cantidad cantidadExtra) {
        Cantidad actual = new Cantidad(this.cantidad);
        this.cantidad = actual.sumar(cantidadExtra).valor();
    }

    // Permite actualizar la cantidad a un nuevo valor (ej: modificar desde el
    // carrito)
    public void actualizarCantidad(Cantidad nuevaCantidad) {
        this.cantidad = nuevaCantidad.valor();
    }

    // Calculamos el subtotal multiplicando el precio unitario por la cantidad
    public Money subtotal() {
        return precioUnitario.multiplicar(cantidad);
    }

    // Getters
    public UUID getProductoId() {
        return productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public String getSku() {
        return sku;
    }

    public Money getPrecioUnitario() {
        return precioUnitario;
    }
}