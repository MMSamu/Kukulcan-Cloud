package com.uamishop.backend.orden.domain;

import com.uamishop.backend.shared.domain.Money;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate Root para la gestión de órdenes.
 * Implementa todas las reglas de negocio relacionadas con el ciclo de vida de
 * una orden.
 */
@Entity
@Table(name = "ordenes")
public class Orden {
    @EmbeddedId
    @AttributeOverride(name = "valor", column = @Column(name = "orden_id"))
    private OrdenId id;

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "cliente_id"))
    private ClienteId clienteId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "orden_id", nullable = false)
    private List<ItemOrden> items = new ArrayList<>();

    @Embedded
    private DireccionEnvio direccionEnvio;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "cantidad", column = @Column(name = "total_cantidad")),
            @AttributeOverride(name = "moneda", column = @Column(name = "total_moneda"))
    })
    private Money total;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_actual", nullable = false)
    private EstadoOrden estadoActual;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago")
    private EstadoPago estadoPago;

    @Column(name = "referencia_pago")
    private String referenciaPago;

    @Column(name = "numero_guia")
    private String numeroGuia;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "historial_estados", joinColumns = @JoinColumn(name = "orden_id"))
    private List<CambioEstado> historialEstados = new ArrayList<>();

    // Constructor sin argumentos requerido por JPA
    protected Orden() {
    }

    private Orden(ClienteId clienteId, List<ItemOrden> items, DireccionEnvio direccionEnvio) {
        this.id = OrdenId.generar();
        this.clienteId = Objects.requireNonNull(clienteId, "El ID del cliente no puede ser nulo");
        this.items = new ArrayList<>(items);
        this.direccionEnvio = Objects.requireNonNull(direccionEnvio, "La dirección de envío no puede ser nula");
        this.total = calcularTotal();
        this.estadoActual = EstadoOrden.PENDIENTE;
        this.estadoPago = EstadoPago.PENDIENTE;
    }

    /**
     * Crea una nueva orden validando todas las reglas de negocio.
     * RN-ORD-01: Debe tener al menos un item
     * RN-ORD-02: El total debe ser mayor a cero
     * RN-ORD-04: Teléfono de 10 dígitos (validado en DireccionEnvio)
     */
    public static Orden crear(ClienteId clienteId, List<ItemOrden> items, DireccionEnvio direccionEnvio) {
        // RN-ORD-01: Debe tener al menos un item
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La orden debe tener al menos un item");
        }

        Orden orden = new Orden(clienteId, items, direccionEnvio);

        // RN-ORD-02: El total debe ser mayor a cero
        if (!orden.total.esPositivo()) {
            throw new IllegalArgumentException("El total de la orden debe ser mayor a cero");
        }

        return orden;
    }

    /**
     * Confirma la orden.
     * RN-ORD-05: El estado actual debe ser PENDIENTE
     * RN-ORD-06: Se registra el cambio en el historial
     */
    public void confirmar() {
        // RN-ORD-05: Validar que el estado actual sea PENDIENTE
        if (this.estadoActual != EstadoOrden.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se pueden confirmar órdenes en estado PENDIENTE. Estado actual: " + this.estadoActual);
        }

        cambiarEstado(EstadoOrden.CONFIRMADA, "Orden confirmada por el cliente");
    }

    /**
     * Procesa el pago de la orden.
     * RN-ORD-07: El estado debe ser CONFIRMADA
     * RN-ORD-08: La referencia de pago no debe estar vacía
     */
    public void procesarPago(String referenciaPago) {
        // RN-ORD-07: Validar que el estado sea CONFIRMADA
        if (this.estadoActual != EstadoOrden.CONFIRMADA) {
            throw new IllegalStateException(
                    "Solo se puede procesar el pago de órdenes CONFIRMADAS. Estado actual: " + this.estadoActual);
        }

        // RN-ORD-08: La referencia de pago no debe estar vacía
        if (referenciaPago == null || referenciaPago.trim().isEmpty()) {
            throw new IllegalArgumentException("La referencia de pago no puede estar vacía");
        }

        this.referenciaPago = referenciaPago;
        this.estadoPago = EstadoPago.COMPLETADO;
        cambiarEstado(EstadoOrden.PREPARACION, "Pago procesado con referencia: " + referenciaPago);
    }

    /**
     * Marca la orden como enviada.
     * RN-ORD-10: El estado debe ser PREPARACION
     * RN-ORD-11: Se requiere número de guía
     * RN-ORD-12: La guía debe tener longitud mínima de 5 caracteres
     */
    public void marcarEnviada(String numeroGuia) {
        // RN-ORD-10: Validar que el estado sea PREPARACION
        if (this.estadoActual != EstadoOrden.PREPARACION) {
            throw new IllegalStateException(
                    "Solo se pueden marcar como enviadas las órdenes en PREPARACION. Estado actual: "
                            + this.estadoActual);
        }

        // RN-ORD-11: Se requiere número de guía
        if (numeroGuia == null || numeroGuia.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de guía es obligatorio");
        }

        // RN-ORD-12: Validar longitud mínima de guía
        if (numeroGuia.trim().length() < 5) {
            throw new IllegalArgumentException("El número de guía debe tener al menos 5 caracteres");
        }

        this.numeroGuia = numeroGuia;
        cambiarEstado(EstadoOrden.ENVIADA, "Orden enviada con guía: " + numeroGuia);
    }

    /**
     * Marca la orden como entregada.
     */
    public void marcarEntregada() {
        if (this.estadoActual != EstadoOrden.ENVIADA) {
            throw new IllegalStateException(
                    "Solo se pueden marcar como entregadas las órdenes ENVIADAS. Estado actual: " + this.estadoActual);
        }

        cambiarEstado(EstadoOrden.ENTREGADA, "Orden entregada al cliente");
    }

    /**
     * Cancela la orden.
     * RN-ORD-14: No se puede cancelar si está ENVIADA o ENTREGADA
     * RN-ORD-15, RN-ORD-16: Se requiere motivo de al menos 10 caracteres
     */
    public void cancelar(String motivo) {
        // RN-ORD-14: Validar que no esté ENVIADA o ENTREGADA
        if (this.estadoActual == EstadoOrden.ENVIADA || this.estadoActual == EstadoOrden.ENTREGADA) {
            throw new IllegalStateException(
                    "No se pueden cancelar órdenes que ya han sido enviadas o entregadas. Estado actual: "
                            + this.estadoActual);
        }

        // RN-ORD-15, RN-ORD-16: El motivo debe tener al menos 10 caracteres
        if (motivo == null || motivo.trim().length() < 10) {
            throw new IllegalArgumentException("El motivo de cancelación debe tener al menos 10 caracteres");
        }

        cambiarEstado(EstadoOrden.CANCELADA, motivo);
    }

    /**
     * Cambia el estado de la orden y registra el cambio en el historial.
     * RN-ORD-06: Registrar el cambio en el historial
     */
    private void cambiarEstado(EstadoOrden nuevoEstado, String motivo) {
        EstadoOrden estadoAnterior = this.estadoActual;

        if (!estadoAnterior.puedeTransicionarA(nuevoEstado)) {
            throw new IllegalStateException(
                    String.format("Transición de estado inválida: %s -> %s", estadoAnterior, nuevoEstado));
        }

        this.estadoActual = nuevoEstado;

        // RN-ORD-06: Registrar el cambio en el historial
        CambioEstado cambio = CambioEstado.registrar(estadoAnterior, nuevoEstado, motivo);
        this.historialEstados.add(cambio);
    }

    /**
     * Calcula el total de la orden sumando los subtotales de todos los items.
     */
    private Money calcularTotal() {
        if (items.isEmpty()) {
            return Money.pesos(0);
        }

        Money total = items.get(0).calcularSubtotal();
        for (int i = 1; i < items.size(); i++) {
            total = total.sumar(items.get(i).calcularSubtotal());
        }
        return total;
    }

    // Getters
    public OrdenId getId() {
        return id;
    }

    public ClienteId getClienteId() {
        return clienteId;
    }

    public List<ItemOrden> getItems() {
        return Collections.unmodifiableList(items);
    }

    public DireccionEnvio getDireccionEnvio() {
        return direccionEnvio;
    }

    public Money getTotal() {
        return total;
    }

    public EstadoOrden getEstadoActual() {
        return estadoActual;
    }

    public EstadoPago getEstadoPago() {
        return estadoPago;
    }

    public String getReferenciaPago() {
        return referenciaPago;
    }

    public String getNumeroGuia() {
        return numeroGuia;
    }

    public List<CambioEstado> getHistorialEstados() {
        return Collections.unmodifiableList(historialEstados);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Orden orden = (Orden) o;
        return Objects.equals(id, orden.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
