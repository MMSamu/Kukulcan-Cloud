package com.uamishop.backend.orden.domain;

import com.uamishop.backend.shared.domain.Money;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;

/**
 * Entidad que representa un item dentro de una orden.
 * Contiene información del producto, cantidad y precio.
 */
@Entity
@Table(name = "items_orden")
public class ItemOrden {
    @EmbeddedId
    @AttributeOverride(name = "valor", column = @Column(name = "item_id"))
    private ItemOrdenId id;

    @Column(name = "producto_id", nullable = false)
    private UUID productoId;

    @Column(name = "nombre_producto", nullable = false)
    private String nombreProducto;

    @Column(name = "sku")
    private String sku;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "cantidad", column = @Column(name = "precio_cantidad")),
            @AttributeOverride(name = "moneda", column = @Column(name = "precio_moneda"))
    })
    private Money precioUnitario;

    // Constructor sin argumentos requerido por JPA
    protected ItemOrden() {
    }

    private ItemOrden(UUID productoId, String nombreProducto, String sku, int cantidad, Money precioUnitario) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        if (precioUnitario == null || !precioUnitario.esPositivo()) {
            throw new IllegalArgumentException("El precio debe ser positivo");
        }

        this.id = ItemOrdenId.generar();
        this.productoId = Objects.requireNonNull(productoId, "El ID del producto no puede ser nulo");
        this.nombreProducto = Objects.requireNonNull(nombreProducto, "El nombre del producto no puede ser nulo");
        this.sku = sku;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public static ItemOrden crear(UUID productoId, String nombreProducto, String sku,
            int cantidad, Money precioUnitario) {
        return new ItemOrden(productoId, nombreProducto, sku, cantidad, precioUnitario);
    }

    public Money calcularSubtotal() {
        return precioUnitario.multiplicar(cantidad);
    }

    public ItemOrdenId getId() {
        return id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ItemOrden itemOrden = (ItemOrden) o;
        return Objects.equals(id, itemOrden.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}