package com.uamishop.backend.ventas.domain;

import java.math.BigDecimal;
import com.uamishop.backend.shared.domain.Money;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Carrito {
    private UUID id;
    private UUID clienteId;
    private List<ItemCarrito> items;
    private EstadoCarrito estado;
    private Money descuento;

    public Carrito(UUID clienteId) {
        this.id = UUID.randomUUID();
        this.clienteId = clienteId;
        this.items = new ArrayList<>();
        this.estado = EstadoCarrito.ACTIVO;
        this.descuento = Money.pesos(0); // Inicializamos descuento en 0
    }


    // 1. Agregar Producto
    public void agregarProducto(UUID productoId, int cantidad, Money precio) {
        validarEstadoActivo();
        if (cantidad <= 0) throw new RuntimeException("La cantidad debe ser mayor a 0");

        Optional<ItemCarrito> existente = items.stream()
                .filter(i -> i.getProductoId().equals(productoId))
                .findFirst();

        if (existente.isPresent()) {
            if (existente.get().getCantidad() + cantidad > 10) {
                throw new RuntimeException("Maximo 10 unidades permitidas por producto");
            }
            existente.get().aumentarCantidad(cantidad);
        } else {
            if (cantidad > 10) throw new RuntimeException("Maximo 10 unidades permitidas por producto");
            if (items.size() >= 20) throw new RuntimeException("Carrito lleno");
            items.add(new ItemCarrito(productoId, cantidad, precio));
        }
    }


    // 2. Modificar Cantidad (Nuevo)
    public void modificarCantidad(UUID productoId, int nuevaCantidad) {
        validarEstadoActivo();
        if (nuevaCantidad <= 0) {
            eliminarProducto(productoId); // Si ponen 0, lo borramos
            return;
        }
        if (nuevaCantidad > 10) throw new RuntimeException("Maximo 10 unidades permitidas");

        ItemCarrito item = items.stream()
                .filter(i -> i.getProductoId().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));

        item.actualizarCantidad(nuevaCantidad);
    }


    // 3. Eliminar Producto
    public void eliminarProducto(UUID productoId) {
        validarEstadoActivo();
        boolean eliminado = items.removeIf(item -> item.getProductoId().equals(productoId));
        if (!eliminado) throw new RuntimeException("Producto no encontrado");
    }


    // 4. Vaciar Carrito
    public void vaciar() {
        validarEstadoActivo();
        items.clear();
        this.descuento = Money.pesos(0); // Reiniciamos descuento
    }


    // 5. Iniciar Checkout
    public void iniciarCheckout() {
        validarEstadoActivo();
        if (items.isEmpty()) throw new RuntimeException("No se puede hacer checkout de un carrito vacio");
        //RN -12 EL TOTAL DEL CARRITO DEBE SER MAYOR A $50
        if (calcularTotal().getCantidad().compareTo(new BigDecimal("50")) <= 0) {
            throw new RuntimeException("El monto mínimo de compra es mayor a $50 pesos");
        }
        this.estado = EstadoCarrito.EN_CHECKOUT;
    }


    // 6. Completar Checkout (Revisalo Lau)
    public void completarCheckout() {
        if (this.estado != EstadoCarrito.EN_CHECKOUT) {
            throw new RuntimeException("El carrito debe estar en checkout para completarse");
        }
        this.estado = EstadoCarrito.COMPLETADO;
    }


    // 7. Abandonar (Revisalo Lau)
    public void abandonar() {
        // RN-VEN-14: Solo se puede abandonar si está en estado EN_CHECKOUT (SE QUITO EL "COMPLETADO")
        if (this.estado != EstadoCarrito.EN_CHECKOUT) {
            throw new RuntimeException("Solo se puede abandonar un carrito que está en proceso de checkout");
        }
        this.estado = EstadoCarrito.ABANDONADO;
    }


    // 8. Aplicar Descuento (Revisalo Lau)
    public void aplicarDescuento(Money montoDescuento) {
        validarEstadoActivo();
        Money subtotal = calcularSubtotal();
        // Falto calcular el tope del 30% del subtotal (subtotal * 0.30)
        BigDecimal limite30 = subtotal.getCantidad().multiply(new BigDecimal("0.30"));
        
        if (montoDescuento.getCantidad().compareTo(limite30) > 0) {
            throw new RuntimeException("El descuento no puede ser mayor al 30% del subtotal");
        }
        this.descuento = montoDescuento;
    }

    // ****** Calculos y auxiliares ******

    public Money calcularTotal() {
        Money subtotal = calcularSubtotal();
        return subtotal.restar(descuento);
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

    // Getters
    public List<ItemCarrito> getItems() { return items; }
    public EstadoCarrito getEstado() { return estado; }
    public UUID getId() { return id; }
    public Money getDescuento() { return descuento; }
}