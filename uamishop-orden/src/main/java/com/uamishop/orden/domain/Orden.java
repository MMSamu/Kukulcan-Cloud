package com.uamishop.orden.domain;

import com.uamishop.shared.domain.Money;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

    // --- CAMBIO AQUÍ: RENOMBRAR COLUMNA INTERNA PARA EVITAR DUPLICADOS ---
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "estado", column = @Column(name = "envio_estado"))
    })
    private DireccionEnvio direccionEnvio;

    @Embedded
    private ResumenPago referenciaPago;

    @Embedded
    private InfoEnvio infoEnvio;

    @Embedded
    private Money subtotal;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "cantidad", column = @Column(name = "descuento_monto")),
            @AttributeOverride(name = "moneda", column = @Column(name = "descuento_moneda"))
    })
    private Money descuento;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "cantidad", column = @Column(name = "total_cantidad")),
            @AttributeOverride(name = "moneda", column = @Column(name = "total_moneda"))
    })
    private Money total;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado") // Este usa la columna "estado" original
    private EstadoOrden estado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "historial_estados", joinColumns = @JoinColumn(name = "orden_id"))
    private List<CambioEstado> historialEstados;

    protected Orden() {
    }

    public Orden(UUID clienteId, DireccionEnvio direccionEnvio) {
        this.id = UUID.randomUUID();
        this.numeroOrden = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.clienteId = clienteId;
        this.items = new ArrayList<>();
        this.estado = EstadoOrden.PENDIENTE;
        this.direccionEnvio = direccionEnvio;
        this.subtotal = Money.pesos(0);
        this.total = Money.pesos(0);
        this.descuento = Money.pesos(0);
        this.fechaCreacion = LocalDateTime.now();
        this.referenciaPago = ResumenPago.pendiente();
        this.historialEstados = new ArrayList<>();
    }

    // --- MÉTODOS PARA EL SERVICE (TOTALES) ---

    public void setTotal(BigDecimal cantidad) {
        this.total = Money.pesos(cantidad.doubleValue());
    }

    public void setSubtotal(BigDecimal cantidad) {
        this.subtotal = Money.pesos(cantidad.doubleValue());
    }

    // --- MÉTODOS DE LÓGICA DE ESTADOS ---

    public void confirmar() {
        if (this.estado != EstadoOrden.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden confirmar órdenes en estado PENDIENTE.");
        }
        cambiarEstado(EstadoOrden.CONFIRMADA, "Orden confirmada por el cliente");
    }

    public void procesarPago(String referenciaPago) {
        if (this.estado != EstadoOrden.CONFIRMADA) {
            throw new IllegalStateException("Solo se puede procesar el pago de órdenes CONFIRMADAS.");
        }
        this.referenciaPago = ResumenPago.completado("PAGO", referenciaPago, LocalDateTime.now());
        cambiarEstado(EstadoOrden.PREPARACION, "Pago procesado con referencia: " + referenciaPago);
    }

    public void marcarEnProceso() {
        if (this.estado != EstadoOrden.CONFIRMADA) {
            throw new IllegalStateException("Solo se pueden marcar como en proceso órdenes CONFIRMADAS.");
        }
        cambiarEstado(EstadoOrden.PREPARACION, "Orden en preparación");
    }

    public void marcarEnviada(String numeroGuia) {
        if (this.estado != EstadoOrden.PREPARACION) {
            throw new IllegalStateException("Solo se pueden marcar como enviadas órdenes en PREPARACION.");
        }
        this.infoEnvio = new InfoEnvio("", numeroGuia, LocalDateTime.now());
        cambiarEstado(EstadoOrden.ENVIADA, "Orden enviada con guía: " + numeroGuia);
    }

    public void marcarEntregada() {
        if (this.estado != EstadoOrden.ENVIADA) {
            throw new IllegalStateException("Solo se pueden marcar como entregadas órdenes ENVIADAS.");
        }
        cambiarEstado(EstadoOrden.ENTREGADA, "Orden entregada al cliente");
    }

    public void cancelar(String motivo) {
        if (this.estado == EstadoOrden.ENVIADA || this.estado == EstadoOrden.ENTREGADA) {
            throw new IllegalStateException("No se pueden cancelar órdenes ya enviadas o entregadas.");
        }
        cambiarEstado(EstadoOrden.CANCELADA, motivo);
    }

    private void cambiarEstado(EstadoOrden nuevoEstado, String motivo) {
        EstadoOrden estadoAnterior = this.estado;
        if (!estadoAnterior.puedeTransicionarA(nuevoEstado)) {
            throw new IllegalStateException(String.format("Transición inválida: %s -> %s", estadoAnterior, nuevoEstado));
        }
        this.estado = nuevoEstado;
        this.historialEstados.add(CambioEstado.registrar(estadoAnterior, nuevoEstado, motivo));
    }

    // --- GETTERS ---

    public OrdenId getId() { 
        return new OrdenId(this.id); 
    }

    public UUID getClienteId() { 
        return this.clienteId; 
    }

    public EstadoOrden getEstado() { 
        return this.estado; 
    }

    public Money getTotal() { 
        return total != null ? total : Money.pesos(0); 
    }

    public Money getDescuento() { 
        return descuento != null ? descuento : Money.pesos(0); 
    }

    public Money calcularSubtotal() { 
        return subtotal != null ? subtotal : Money.pesos(0); 
    }

    public LocalDateTime getFechaCreacion() { 
        return fechaCreacion; 
    }

    public DireccionEnvio getDireccionEnvio() { 
        return direccionEnvio; 
    }

    public ResumenPago getResumenPago() { 
        return referenciaPago; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Orden orden)) return false;
        return Objects.equals(id, orden.id);
    }

    @Override
    public int hashCode() { 
        return Objects.hash(id); 
    }
}