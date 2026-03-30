package com.uamishop.backend.orden.service;

import com.uamishop.backend.shared.exception.DomainException;
import com.uamishop.backend.orden.config.RabbitConfig;
import com.uamishop.backend.orden.controller.dto.DatosResumen;
import com.uamishop.backend.orden.controller.dto.OrdenResumen;
import com.uamishop.backend.orden.domain.DireccionEnvio;
import com.uamishop.backend.orden.domain.Orden;
import com.uamishop.backend.orden.repository.OrdenJpaRepository;
import com.uamishop.backend.shared.event.OrdenCreadaEvent;
import com.uamishop.backend.shared.event.ProductoCompradoEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public OrdenResumen crear(UUID clienteId, DireccionEnvio direccionEnvio) {
        Orden orden = new Orden(clienteId, direccionEnvio);
        Orden guardada = ordenRepository.save(orden);

        // 2. Notificar a CATÁLOGO (RK_PRODUCTO_COMPRADO)
        ProductoCompradoEvent eventoCatalogo = new ProductoCompradoEvent(
                UUID.randomUUID(),
                Instant.now(),
                guardada.getId().valor(), // Extraemos el UUID
                clienteId,
                guardada.getItems().stream()
                        .map(item -> new ProductoCompradoEvent.ItemComprado(
                                item.getProductoId(),
                                item.getSku(),
                                item.getCantidad(),
                                item.getPrecioUnitario().getCantidad(),
                                item.getPrecioUnitario().getMoneda()))
                        .toList());

        rabbitTemplate.convertAndSend(
                RabbitConfig.EVENTS_EXCHANGE,
                RabbitConfig.RK_PRODUCTO_COMPRADO,
                eventoCatalogo);

        // 3. Notificar a VENTAS (Cumpliendo con los 5 parámetros del Record)
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

        return OrdenResumen.desde(guardada);
    }

    @Transactional
    public OrdenResumen crearDesdeCarrito(UUID carritoId, DireccionEnvio direccionEnvio) {

        throw new UnsupportedOperationException("La creación desde carrito debe ser orquestada por el micro de Ventas.");
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
}