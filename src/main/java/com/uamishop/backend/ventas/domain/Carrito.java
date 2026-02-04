package com.uamishop.backend.ventas.domain;

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

    public Carrito(UUID clienteId) {
        this.id = UUID.randomUUID();
        this.clienteId = clienteId;
        this.items = new ArrayList<>();
        this.estado = EstadoCarrito.ACTIVO;
    }

    public void agregarProducto(UUID productoId, int cantidad, Money precio) {
        // Regla: Solo si esta activo
        if (this.estado != EstadoCarrito.ACTIVO) {
            throw new RuntimeException("El carrito no esta activo");
        }

        // Regla: Cantidad positiva
        if (cantidad <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a 0");
        }

        // Busca si ya existe el producto
        Optional<ItemCarrito> existente = items.stream()
                .filter(i -> i.getProductoId().equals(productoId))
                .findFirst();

        if (existente.isPresent()) {
            // Regla: Maximo 10 unidades por producto
            if (existente.get().getCantidad() + cantidad > 10) {
                throw new RuntimeException("Maximo 10 unidades permitidas por producto");
            }
            existente.get().aumentarCantidad(cantidad);
        } else {
            // Regla: Maximo 10 unidades (validación inicial para producto nuevo)
            if (cantidad > 10) {
                throw new RuntimeException("Maximo 10 unidades permitidas por producto");
            }

            // Regla: Carrito lleno (max 20 productos distintos)
            if (items.size() >= 20) {
                throw new RuntimeException("Carrito lleno");
            }
            items.add(new ItemCarrito(productoId, cantidad, precio));
        }
    }

    public List<ItemCarrito> getItems() {
        return items;
    }

    public EstadoCarrito getEstado() {
        return estado;
    }

    public UUID getId() {
        return id;
    }

    public UUID getClienteId() {
        return clienteId;
    }
}
