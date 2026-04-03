package com.uamishop.orden.service;

import com.uamishop.shared.exception.DomainException;
import com.uamishop.orden.config.RabbitConfig;
import com.uamishop.orden.controller.dto.DatosResumen;
import com.uamishop.orden.controller.dto.OrdenResumen;
import com.uamishop.orden.domain.DireccionEnvio;
import com.uamishop.orden.domain.Orden;
import com.uamishop.orden.repository.OrdenJpaRepository;
import com.uamishop.shared.event.OrdenCreadaEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class OrdenService {

    private final OrdenJpaRepository ordenRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RabbitTemplate rabbitTemplate;

    public OrdenService(OrdenJpaRepository ordenRepository, 
                        ApplicationEventPublisher eventPublisher, 
                        RabbitTemplate rabbitTemplate) {
        this.ordenRepository = ordenRepository;
        this.eventPublisher = eventPublisher;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional(readOnly = true)
    public OrdenResumen obtenerOrden(UUID ordenId) {
        return OrdenResumen.desde(buscarPorId(ordenId));
    }

    @Transactional(readOnly = true)
    public List<OrdenResumen> listarOrdenes() {
        return todasLasOrdenes().stream()
                .map(OrdenResumen::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DatosResumen> listarDatos() {
        return todasLasOrdenes().stream()
                .map(this::mapearDatosResumen)
                .toList();
    }

    @Transactional
    public OrdenResumen crear(UUID clienteId, DireccionEnvio direccionEnvio, BigDecimal monto) {
        Orden orden = new Orden(clienteId, direccionEnvio);
        
        // Asignación de montos reales recibidos
        orden.setTotal(monto);
        orden.setSubtotal(monto);
        
        Orden guardada = ordenRepository.save(orden);
        notificarOrdenCreada(guardada, clienteId);

        return OrdenResumen.desde(guardada);
    }

    /**
     * Crea una orden a partir de un evento de carrito finalizado.
     * Es crucial para la integración asíncrona con Ventas.
     */
    @Transactional
    public OrdenResumen crearDesdeCarrito(UUID clienteId, DireccionEnvio direccionEnvio, BigDecimal monto) {
        Orden orden = new Orden(clienteId, direccionEnvio);
        
        // Sincronizamos el total del carrito con la nueva orden
        orden.setTotal(monto);
        orden.setSubtotal(monto);
        
        Orden guardada = ordenRepository.save(orden);
        
        // Notificamos que la orden existe (útil para que Ventas sepa que debe limpiar el carrito)
        notificarOrdenCreada(guardada, clienteId);
        
        return OrdenResumen.desde(guardada);
    }

    private void notificarOrdenCreada(Orden guardada, UUID clienteId) {
        OrdenCreadaEvent eventoVentas = new OrdenCreadaEvent(
                UUID.randomUUID(),
                Instant.now(),
                guardada.getId().valor(),
                clienteId,
                null
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.EVENTS_EXCHANGE,
                RabbitConfig.RK_ORDEN_CREADA,
                eventoVentas);
    }

    @Transactional
    public OrdenResumen confirmar(UUID ordenId) {
        Orden orden = buscarPorId(ordenId);
        orden.confirmar();
        return OrdenResumen.desde(ordenRepository.save(orden));
    }

    @Transactional
    public OrdenResumen procesarPago(UUID ordenId, String referenciaPago) {
        Orden orden = buscarPorId(ordenId);
        orden.procesarPago(referenciaPago);
        return OrdenResumen.desde(ordenRepository.save(orden));
    }

    @Transactional
    public OrdenResumen marcarEnProceso(UUID ordenId) {
        Orden orden = buscarPorId(ordenId);
        orden.marcarEnProceso();
        return OrdenResumen.desde(ordenRepository.save(orden));
    }

    @Transactional
    public OrdenResumen marcarEnviada(UUID ordenId, String numeroGuia) {
        Orden orden = buscarPorId(ordenId);
        orden.marcarEnviada(numeroGuia);
        return OrdenResumen.desde(ordenRepository.save(orden));
    }

    @Transactional
    public OrdenResumen marcarEntregada(UUID ordenId) {
        Orden orden = buscarPorId(ordenId);
        orden.marcarEntregada();
        return OrdenResumen.desde(ordenRepository.save(orden));
    }

    @Transactional
    public OrdenResumen cancelar(UUID ordenId, String motivo) {
        Orden orden = buscarPorId(ordenId);
        orden.cancelar(motivo);
        return OrdenResumen.desde(ordenRepository.save(orden));
    }

    @Transactional(readOnly = true)
    public Orden buscarPorId(UUID ordenId) {
        return ordenRepository.findById(ordenId)
                .orElseThrow(() -> new DomainException("Orden no encontrada con id: " + ordenId));
    }

    @Transactional(readOnly = true)
    public List<Orden> todasLasOrdenes() {
        return ordenRepository.findAll();
    }

    private DatosResumen mapearDatosResumen(Orden orden) {
        DireccionEnvio dir = orden.getDireccionEnvio();
        String fecha = (orden.getFechaCreacion() != null) ? orden.getFechaCreacion().toLocalDate().toString() : "";
        String hora = (orden.getFechaCreacion() != null) ? orden.getFechaCreacion().toLocalTime().toString() : "";
        
        String metodoPago = "N/A";
        String formaPago = "N/A";
        if (orden.getResumenPago() != null) {
            metodoPago = orden.getResumenPago().getMetodoPago();
            formaPago = orden.getResumenPago().getReferenciaExterna();
        }

        return new DatosResumen(
                orden.getClienteId(),
                "Cliente", "Sistema", 
                orden.getEstado().name(),
                (dir != null ? dir.toString() : "Dirección no disponible"),
                (dir != null ? dir.getTelefonoContacto() : "N/A"),
                metodoPago,
                formaPago,
                fecha,
                hora);
    }

    @Transactional
    public void actualizarMontoOrden(UUID ordenId, BigDecimal monto) {
        Orden orden = buscarPorId(ordenId);
        orden.setTotal(monto);
        orden.setSubtotal(monto);
        ordenRepository.save(orden);
        System.out.println("✅ Orden " + ordenId + " actualizada con total: $" + monto);
    }
}