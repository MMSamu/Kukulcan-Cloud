package com.uamishop.backend.ventas.domain;

import com.uamishop.backend.shared.domain.Money;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/* Representa un carrito de compras */
@Entity
@Table(name = "carritos")
public class Carrito {

    // Atributos
    @Id
    @Column(name = "id")
    private UUID id;

    // Relacion con cliente (solo guardamos el ID)
    @Column(name = "cliente_id")
    private UUID clienteId;

    // Relacion con items del carrito
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "carrito_id")
    private List<ItemCarrito> items;

    // Estado del carrito
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoCarrito estado;

    // Descuento aplicado al carrito
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "cantidad", column = @Column(name = "descuento_monto")),
            @AttributeOverride(name = "moneda", column = @Column(name = "descuento_moneda"))
    })

    // El descuento se almacena como un Money embebido, con columnas personalizadas
    private Money descuento;

    // Constructor vacío para JPA
    protected Carrito() { }

    // Constructor para crear un nuevo carrito
    public Carrito(UUID clienteId) {
        this.id = UUID.randomUUID();
        this.clienteId = clienteId;
        this.items = new ArrayList<>();
        this.estado = EstadoCarrito.ACTIVO;
        this.descuento = Money.pesos(0);
    }

    // Metodos de negocio
    /* Agregar producto al carrito */
    public void agregarProducto(UUID productoId, int cantidadInt, Money precio) {
        validarEstadoActivo();
        Cantidad cantidad = new Cantidad(cantidadInt);

        // Verificar si el producto ya existe en el carrito
        Optional<ItemCarrito> existente = items.stream()
                .filter(i -> i.getProductoId().equals(productoId))
                .findFirst();

        // Si ya existe, aumentar la cantidad. Si no, agregar un nuevo item al carrito
        if (existente.isPresent()) {
            existente.get().aumentarCantidad(cantidad);
        } else {
            if (items.size() >= 20) throw new RuntimeException("Carrito lleno");
            items.add(new ItemCarrito(productoId, cantidadInt, precio));
        }
    }

    /* Modificar cantidad de un producto en el carrito */
    public void modificarCantidad(UUID productoId, int nuevaCantidadInt) {
        validarEstadoActivo();

        // Si la nueva cantidad es 0 o negativa, se elimina el producto del carrito
        if (nuevaCantidadInt <= 0) {
            eliminarProducto(productoId);
            return;
        }
        // Si la cantidad es positiva, se actualiza la cantidad del producto en el carrito
        Cantidad nuevaCantidad = new Cantidad(nuevaCantidadInt);
        // Buscar el item en el carrito
        ItemCarrito item = items.stream()
                .filter(i -> i.getProductoId().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en el carrito"));
        item.actualizarCantidad(nuevaCantidad);
    }

    /* Eliminar producto del carrito */
    public void eliminarProducto(UUID productoId) {
        validarEstadoActivo();
        // Elimina el producto del carrito usando removeIf, que devuelve true si se eliminó algún elemento
        boolean eliminado = items.removeIf(item -> item.getProductoId().equals(productoId));
        if (!eliminado) throw new RuntimeException("Producto no encontrado");
    }

    /* Vaciar el carrito */
    public void vaciar() {
        validarEstadoActivo();
        items.clear();
        this.descuento = Money.pesos(0);
    }

    /* Iniciar el proceso de checkout */
    public void iniciarCheckout() {
        validarEstadoActivo();
        // Para iniciar el checkout, el carrito debe tener al menos un producto y un total mínimo de 50 pesos
        if (items.isEmpty()) throw new RuntimeException("No se puede hacer checkout de un carrito vacio");
        // El total se calcula restando el descuento al subtotal, y debe ser al menos 50 pesos para iniciar el checkout
        if (calcularTotal().getCantidad().compareTo(new BigDecimal("50")) < 0) {
            throw new RuntimeException("El monto minimo de compra es de 50 pesos");
        }
        this.estado = EstadoCarrito.EN_CHECKOUT;
    }

    /* Completar el proceso de checkout */
    public void completarCheckout() {
        // Para completar el checkout, el carrito debe estar en estado EN_CHECKOUT
        if (this.estado != EstadoCarrito.EN_CHECKOUT) {
            throw new RuntimeException("El carrito debe estar en checkout para completarse");
        }
        this.estado = EstadoCarrito.COMPLETADO;
    }

    /* Abandonar el carrito */
    public void abandonar() {
        // Solo se puede abandonar un carrito que esté en proceso de checkout, para evitar que se abandonen carritos activos o ya completados
        if (this.estado != EstadoCarrito.EN_CHECKOUT) {
            throw new RuntimeException("Solo se puede abandonar un carrito en proceso de checkout");
        }
        this.estado = EstadoCarrito.ABANDONADO;
    }

    /* Aplicar descuento */
    public void aplicarDescuento(Money montoDescuento) {
        validarEstadoActivo();
        // El descuento no puede ser negativo ni mayor al 30% del subtotal del carrito
        Money subtotal = calcularSubtotal();
        // El límite de descuento se calcula como el 30% del subtotal, y se compara con el monto del descuento para validar que no exceda ese límite
        BigDecimal limite = subtotal.getCantidad().multiply(new BigDecimal("0.30"));
        // Si el monto del descuento es mayor al límite permitido, se lanza una excepción para indicar que el descuento no es válido
        if (montoDescuento.getCantidad().compareTo(limite) > 0) {
            throw new RuntimeException("El descuento no puede ser mayor al 30% del subtotal");
        }
        this.descuento = montoDescuento;
    }

    /* Calcular el total del carrito */
    public Money calcularTotal() {
        return calcularSubtotal().restar(descuento);
    }

    /* Calcular el subtotal del carrito
    Utiliza stream para calcular el subtotal de cada item y reducirlo a un único valor total */
    private Money calcularSubtotal() {
        return items.stream()
                .map(ItemCarrito::subtotal)
                .reduce(Money.pesos(0), Money::sumar);
    }

    /* valida el estado del carrito */
    private void validarEstadoActivo() {
        // Solo se pueden modificar carritos que estén en estado ACTIVO,
        // para evitar cambios en carritos que ya están en checkout, completados o abandonados
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