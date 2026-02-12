package com.uamishop.backend.ventas.domain;

import com.uamishop.backend.shared.domain.Money;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "carritos")
public class Carrito {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "cliente_id")
    private UUID clienteId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "carrito_id")
    private List<ItemCarrito> items;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoCarrito estado;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "cantidad", column = @Column(name = "descuento_monto")),
            @AttributeOverride(name = "moneda", column = @Column(name = "descuento_moneda"))
    })
    private Money descuento;

    // Constructor vacío para JPA
    protected Carrito() { }

    public Carrito(UUID clienteId) {
        this.id = UUID.randomUUID();
        this.clienteId = clienteId;
        this.items = new ArrayList<>();
        this.estado = EstadoCarrito.ACTIVO;
        this.descuento = Money.pesos(0);
    }

    // Metodos de negocio

    public void agregarProducto(UUID productoId, int cantidadInt, Money precio) {
        validarEstadoActivo();
        Cantidad cantidad = new Cantidad(cantidadInt); // Validacion

        Optional<ItemCarrito> existente = items.stream()
                .filter(i -> i.getProductoId().equals(productoId))
                .findFirst();

        if (existente.isPresent()) {
            existente.get().aumentarCantidad(cantidad);
        } else {
            if (items.size() >= 20) throw new RuntimeException("Carrito lleno");
            items.add(new ItemCarrito(productoId, cantidadInt, precio));
        }
    }


    public void modificarCantidad(UUID productoId, int nuevaCantidadInt) {
        validarEstadoActivo();
        if (nuevaCantidadInt <= 0) {
            eliminarProducto(productoId);
            return;
        }
        Cantidad nuevaCantidad = new Cantidad(nuevaCantidadInt);
        ItemCarrito item = items.stream()
                .filter(i -> i.getProductoId().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));
        item.actualizarCantidad(nuevaCantidad);
    }


    public void eliminarProducto(UUID productoId) {
        validarEstadoActivo();
        boolean eliminado = items.removeIf(item -> item.getProductoId().equals(productoId));
        if (!eliminado) throw new RuntimeException("Producto no encontrado");
    }


    public void vaciar() {
        validarEstadoActivo();
        items.clear();
        this.descuento = Money.pesos(0);
    }


    public void iniciarCheckout() {
        validarEstadoActivo();
        if (items.isEmpty()) throw new RuntimeException("No se puede hacer checkout de un carrito vacio");
        if (calcularTotal().getCantidad().compareTo(new BigDecimal("50")) < 0) {
            throw new RuntimeException("El monto minimo de compra es de 50 pesos");
        }
        this.estado = EstadoCarrito.EN_CHECKOUT;
    }


    public void completarCheckout() {
        if (this.estado != EstadoCarrito.EN_CHECKOUT) {
            throw new RuntimeException("El carrito debe estar en checkout para completarse");
        }
        this.estado = EstadoCarrito.COMPLETADO;
    }


    public void abandonar() {
        if (this.estado != EstadoCarrito.EN_CHECKOUT) {
            throw new RuntimeException("Solo se puede abandonar un carrito en proceso de checkout");
        }
        this.estado = EstadoCarrito.ABANDONADO;
    }


    public void aplicarDescuento(Money montoDescuento) {
        validarEstadoActivo();
        Money subtotal = calcularSubtotal();
        BigDecimal limite = subtotal.getCantidad().multiply(new BigDecimal("0.30"));
        if (montoDescuento.getCantidad().compareTo(limite) > 0) {
            throw new RuntimeException("El descuento no puede ser mayor al 30% del subtotal");
        }
        this.descuento = montoDescuento;
    }


    public Money calcularTotal() {
        return calcularSubtotal().restar(descuento);
    }


    private Money calcularSubtotal() {
        return items.stream()
                .map(ItemCarrito::subtotal)
                .reduce(Money.pesos(0), Money::sumar);
    }


    private void validarEstadoActivo() {
        if (this.estado != EstadoCarrito.ACTIVO) {
            throw new RuntimeException("El carrito no esta activo");
        }
    }


    // Getters adaptados
    public CarritoId getId() { return new CarritoId(this.id); } // Retorna Record
    public UUID getClienteId() { return clienteId; }
    public List<ItemCarrito> getItems() { return items; }
    public EstadoCarrito getEstado() { return estado; }
    public Money getDescuento() { return descuento; }
}