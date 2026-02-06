package com.uamishop.backend.orden.domain;

import com.uamishop.backend.shared.domain.Money;
import com.uamishop.backend.ventas.domain.EstadoCarrito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Orden {
    private UUID id;
    private UUID numeroOrden;
    private UUID clienteId;
    private List<ItemOrden> items;
    private String direccionEnvio;
    private String telefonoContacto;
    private EstadoOrden estado;

    private Orden(UUID clienteId) {
        this.id = UUID.randomUUID();
        this.numeroOrden = UUID.randomUUID();
        this.clienteId = clienteId;
        this.items = new ArrayList<>();
    }

    public void crear(UUID clienteId, List<ItemOrden> items, String direccionEnvio, String telefonoContacto) {
        // Regla: Deber tener al menos un item
        if (items.isEmpty()) {
            throw new IllegalArgumentException("La orden debe tener al menos un item");
        }

        // Regla: El total de la orden debe de ser mayor a cero
        Money total = items.stream()
                .map(ItemOrden::getSubtotal)
                .reduce(Money.ZERO, Money::sumar);
        if (total.compareTo(Money.ZERO) <= 0) {
            throw new IllegalArgumentException("El total de la orden debe de ser mayor a cero");
        }

        // Regla: La direccion de envio debe de ser mayor a 5 digitos
        if (direccionEnvio.length() < 5) {
            throw new IllegalArgumentException("La direccion de envio debe de ser mayor a 5 digitos");
        }

        // Regla: El telefono de contacto debe de ser mayor a 5 digitos
        if (telefonoContacto.length() < 10) {
            throw new IllegalArgumentException("El telefono de contacto debe de ser mayor a 10 digitos");
        }
    }

    public void procesarPago() {

    }

    public void marcarEnProceso() {

    }

    public void marcarEnviada() {

    }

    public void marcarEntregada() {

    }

    public void cancelar() {

    }

    public EstadoOrden obtenerEstadoActual() {
        return estado;
    }

    public List<ItemOrden> getItems() {
        return items;
    }

    public UUID getId() {
        return id;
    }

    public UUID getClienteId() {
        return clienteId;
    }
}
