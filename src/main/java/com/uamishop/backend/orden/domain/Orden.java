package com.uamishop.backend.orden.domain;

import com.uamishop.backend.shared.domain.Money;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import java.util.UUID;

/**
 * Aggregate Root para la gestión de órdenes.
 * Implementa todas las reglas de negocio relacionadas con el ciclo de vida de
 * una orden.
 */
@Entity
@Table(name = "ordenes")
public class Orden {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "numero_orden")
    private String numeroOrden;

    @Column(name = "cliente_id")
    private UUID clienteId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "orden_id", nullable = false)
    private List<ItemOrden> items = new ArrayList<>();

    @Embedded
    private DireccionEnvio direccionEnvio;

    @Embedded
    private ResumenPago estadoPago;

    @Embedded
    private ResumenPago referenciaPago;

    @Embedded
    private InfoEnvio infoEnvio;

    @Embedded
    private Money subtotal;

    // Sobreescribe los atributos de Money para el descuento
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "cantidad", column = @Column(name = "descuento_monto")),
            @AttributeOverride(name = "moneda", column = @Column(name = "descuento_moneda"))
    })
    private Money descuento;

    // Sobreescribe los atributos de Money para el total
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "cantidad", column = @Column(name = "total_cantidad")),
            @AttributeOverride(name = "total_moneda", column = @Column(name = "total_moneda"))
    })
    private Money total;

    // Estado de la orden
    @Embedded
    private EstadoOrden estado;

    // Fecha de creación
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    // Historial de cambios de estado
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "historial_estados", joinColumns = @JoinColumn(name = "orden_id"))
    private List<CambioEstado> historialEstados;

    // Constructor sin argumentos requerido por JPA
    protected Orden() {
    }

    // Constructor para crear una nueva orden
    public Orden(UUID clienteId) {
        this.id = UUID.randomUUID();
        this.clienteId = clienteId;
        this.items = new ArrayList<>();
        this.estado = EstadoOrden.PENDIENTE;
        this.direccionEnvio = new DireccionEnvio();
        this.total = Money.pesos(0);
        this.descuento = Money.pesos(0);
    }

    // Constructor para crear una nueva orden con dirección de envío
    public Orden(UUID clienteId, DireccionEnvio direccionEnvio) {
        this.id = UUID.randomUUID();
        this.clienteId = clienteId;
        this.items = new ArrayList<>();
        this.estado = EstadoOrden.PENDIENTE;
        this.direccionEnvio = direccionEnvio;
        this.total = Money.pesos(0);
        this.descuento = Money.pesos(0);
    }

    /**
     * Crea una nueva orden validando todas las reglas de negocio.
     * RN-ORD-01: Debe tener al menos un item
     * RN-ORD-02: El total debe ser mayor a cero
     * RN-ORD-03: El pago debe ser mayor a cero
     * RN-ORD-04: Teléfono de 10 dígitos (validado en DireccionEnvio)
     */
    public void crear(UUID clienteId, List<ItemOrden> items, DireccionEnvio direccion, Money pago) {
        // RN-ORD-01: Debe tener al menos un item
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La orden debe tener al menos un item");
        }
        // RN-ORD-02: El total debe ser mayor a cero
        if (!pago.esPositivo()) {
            throw new IllegalArgumentException("El pago debe ser mayor a cero");
        }
        // RN-ORD-03: El pago debe ser mayor a cero
        if (!direccion.esValido()) {
            throw new IllegalArgumentException("La direccion de envio debe ser valida");
        }
    }

    /**
     * Confirma la orden.
     * RN-ORD-05: El estado actual debe ser PENDIENTE
     * RN-ORD-06: Se registra el cambio en el historial
     */
    public void confirmar() {
        // RN-ORD-05: Validar que el estado actual sea PENDIENTE
        if (this.estado != EstadoOrden.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se pueden confirmar órdenes en estado PENDIENTE. Estado actual: " + this.estado);
        }
        // Cambia el estado a CONFIRMADA
        cambiarEstado(EstadoOrden.CONFIRMADA, "Orden confirmada por el cliente");
    }

    /**
     * Procesa el pago de la orden.
     * RN-ORD-07: El estado debe ser CONFIRMADA
     * RN-ORD-08: La referencia de pago no debe estar vacía
     */
    public void procesarPago(String referenciaPago) {
        // Si el estado actual no es CONFIRMADA, lanza una excepción
        if (this.estado != EstadoOrden.CONFIRMADA) {
            throw new IllegalStateException(
                    "Solo se puede procesar el pago de órdenes CONFIRMADAS. Estado actual: " + this.estado);
        }
        // Si la referencia de pago es nula o está vacía, lanza una excepción
        if (referenciaPago == null || referenciaPago.trim().isEmpty()) {
            throw new IllegalArgumentException("La referencia de pago no puede estar vacía");
        }
        // Registra el pago
        this.referenciaPago = ResumenPago.completado("PAGO", referenciaPago, LocalDateTime.now());
        // Cambia el estado a PREPARACION
        cambiarEstado(EstadoOrden.PREPARACION, "Pago procesado con referencia: " + referenciaPago);
    }

    /**
     * Marca la orden como enviada.
     * RN-ORD-10: El estado debe ser PREPARACION
     * RN-ORD-11: Se requiere número de guía
     * RN-ORD-12: La guía debe tener longitud mínima de 5 caracteres
     */
    public void marcarEnviada(String numeroGuia) {
        // Si el estado actual no es PREPARACION, lanza una excepción
        if (this.estado != EstadoOrden.PREPARACION) {
            throw new IllegalStateException(
                    "Solo se pueden marcar como enviadas las órdenes en PREPARACION. Estado actual: "
                            + this.estado);
        }

        // Si el número de guía es nulo o está vacío, lanza una excepción
        if (numeroGuia == null || numeroGuia.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de guía es obligatorio");
        }

        // Si la longitud del número de guía es menor a 5, lanza una excepción
        if (numeroGuia.trim().length() < 5) {
            throw new IllegalArgumentException("El número de guía debe tener al menos 5 caracteres");
        }

        this.infoEnvio = new InfoEnvio("", numeroGuia, LocalDateTime.now());
        cambiarEstado(EstadoOrden.ENVIADA, "Orden enviada con guía: " + numeroGuia);
    }

    public void marcarEnProceso() {
        // Si el estado actual no es CONFIRMADA, lanza una excepción
        if (this.estado != EstadoOrden.CONFIRMADA) {
            throw new IllegalStateException(
                    "Solo se pueden marcar como en proceso las órdenes CONFIRMADAS. Estado actual: " + this.estado);
        }
        // Cambia el estado a PREPARACION
        cambiarEstado(EstadoOrden.PREPARACION, "Orden en preparación");
    }

    /**
     * Marca la orden como entregada.
     */
    public void marcarEntregada() {
        // Si el estado actual no es ENVIADA, lanza una excepción
        if (this.estado != EstadoOrden.ENVIADA) {
            throw new IllegalStateException(
                    "Solo se pueden marcar como entregadas las órdenes ENVIADAS. Estado actual: " + this.estado);
        }
        // Cambia el estado a ENTREGADA
        cambiarEstado(EstadoOrden.ENTREGADA, "Orden entregada al cliente");
    }

    /**
     * Cancela la orden.
     * RN-ORD-14: No se puede cancelar si está ENVIADA o ENTREGADA
     * RN-ORD-15, RN-ORD-16: Se requiere motivo de al menos 10 caracteres
     */
    public void cancelar(String motivo) {
        // Si el estado actual no es ENVIADA o ENTREGADA, lanza una excepción
        if (this.estado == EstadoOrden.ENVIADA || this.estado == EstadoOrden.ENTREGADA) {
            throw new IllegalStateException(
                    "No se pueden cancelar órdenes que ya han sido enviadas o entregadas. Estado actual: "
                            + this.estado);
        }

        // Si el motivo es nulo o está vacío, lanza una excepción
        if (motivo == null || motivo.trim().length() < 10) {
            throw new IllegalArgumentException("El motivo de cancelación debe tener al menos 10 caracteres");
        }
        // Cambia el estado a CANCELADA
        cambiarEstado(EstadoOrden.CANCELADA, motivo);
    }

    /**
     * Cambia el estado de la orden y registra el cambio en el historial.
     * RN-ORD-06: Registrar el cambio en el historial
     */
    private void cambiarEstado(EstadoOrden nuevoEstado, String motivo) {
        EstadoOrden estadoAnterior = this.estado;

        if (!estadoAnterior.puedeTransicionarA(nuevoEstado)) {
            throw new IllegalStateException(
                    String.format("Transición de estado inválida: %s -> %s", estadoAnterior, nuevoEstado));
        }

        this.estado = nuevoEstado;

        // Registra el cambio en el historial
        CambioEstado cambio = CambioEstado.registrar(estadoAnterior, nuevoEstado, motivo);
        this.historialEstados.add(cambio);
    }

    // Getters
    public OrdenId getId() {
        return new OrdenId(this.id);
    }

    public String numeroOrden() {
        return this.numeroOrden;
    }

    public UUID getClienteId() {
        return this.clienteId;
    }

    public EstadoOrden getEstado() {
        return this.estado;
    }

    public List<ItemOrden> getItems() {
        return Collections.unmodifiableList(items);
    }

    public DireccionEnvio direccionEnvio() {
        return direccionEnvio;
    }

    public ResumenPago resumenPago() {
        return estadoPago;
    }

    public InfoEnvio infoEnvio() {
        return infoEnvio;
    }

    // Calcula el subtotal de la orden sumando el subtotal de cada item
    public Money calcularSubtotal() {
        // Devuelve el subtotal de la orden
        return items.stream()
                .map(ItemOrden::calcularSubtotal)
                .reduce(Money.pesos(0), Money::sumar);
    }

    // Agrega un item a la orden
    public void agregarItem(ItemOrden item) {
        // Si el item es nulo, lanza una excepción
        if (item == null) {
            throw new IllegalArgumentException("El item no puede ser nulo");
        }
        // Agrega el item a la orden
        this.items.add(item);
        this.total = calcularTotal();
    }

    // Aplica un descuento a la orden
    public void aplicarDescuento(Money descuento) {
        // Valida que el estado de la orden sea válido
        validarEstado();
        // Calcula el subtotal de la orden
        Money subtotal = calcularSubtotal();

        // Si el descuento es nulo o está vacío, lanza una excepción
        if (!descuento.esPositivo()) {
            throw new IllegalArgumentException("El descuento debe ser mayor a cero");
        }

        // Si el descuento es mayor al subtotal, lanza una excepción
        if (descuento.esMayorQue(subtotal)) {
            throw new IllegalArgumentException("El descuento no puede ser mayor al subtotal");
        }

        this.descuento = descuento;
        this.total = calcularTotal();
    }

    // Aplica un descuento a la orden
    public void aplicarDescuento(double porcentaje) {
        // Si el porcentaje es nulo o está vacío, lanza una excepción
        if (porcentaje <= 0 || porcentaje > 100) {
            throw new IllegalArgumentException("El porcentaje debe estar entre 0 y 100");
        }

        // Calcula el subtotal de la orden
        Money subtotal = calcularSubtotal();

        // Calcula el descuento sobre el subtotal
        Money descuentoCalculado = subtotal.porcentaje(porcentaje);

        aplicarDescuento(descuentoCalculado);
    }

    // Devuelve el descuento de la orden
    public Money getDescuento() {
        return descuento != null ? descuento : Money.pesos(0);
    }

    // Devuelve el total de la orden
    public Money getTotal() {
        return total != null ? total : Money.pesos(0);
    }

    /**
     * Calcula el total de la orden sumando los subtotales de todos los items.
     */
    private Money calcularTotal() {
        if (items.isEmpty()) {
            return Money.pesos(0);
        }

        Money subtotal = calcularSubtotal();

        // Si no hay descuento o es mayor al subtotal (safety check), retornamos
        // subtotal
        if (descuento == null) {
            return subtotal;
        }

        // El total es subtotal - descuento
        // Nota: Money.restar valida que no quede negativo si así está implementado,
        // pero nuestra validación en aplicarDescuento ya asegura que descuento <=
        // subtotal
        return subtotal.restar(descuento);
    }

    // Valida que el estado de la orden sea válido
    private void validarEstado() {
        // Si el estado actual no es PENDIENTE, lanza una excepción
        if (this.estado != EstadoOrden.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo se pueden modificar órdenes en estado PENDIENTE. Estado actual: "
                            + this.estado);
        }
    }

    // Devuelve la fecha de creación de la orden
    private LocalDateTime fechaCreacion() {
        return fechaCreacion;
    }

    // Devuelve el hash code de la orden
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
