package com.uamishop.backend.orden.service;

import com.uamishop.backend.shared.exception.DomainException;
import com.uamishop.backend.ventas.api.CarritoResumen;
import com.uamishop.backend.ventas.api.VentasApi;
import com.uamishop.backend.RabbitConfig;
import com.uamishop.backend.orden.api.DatosResumen;
import com.uamishop.backend.orden.api.OrdenesApi;
import com.uamishop.backend.orden.api.OrdenResumen;
import com.uamishop.backend.orden.domain.DireccionEnvio;
import com.uamishop.backend.orden.domain.ItemOrden;
import com.uamishop.backend.orden.domain.Orden;
import com.uamishop.backend.orden.repository.OrdenJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.uamishop.backend.shared.event.OrdenCreadaEvent;

import com.uamishop.backend.shared.event.ProductoCompradoEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Implementación del servicio de órdenes.
 *
 * Regla de encapsulamiento:
 * - Los métodos declarados en OrdenesApi son la interfaz pública del módulo.
 * - El resto de los métodos son package-private (o privados) y solo pueden
 * ser usados internamente.
 */
@Service
public class OrdenService implements OrdenesApi {

    // ── Dependencias ──────────────────────────────────────────────────────────

    private final OrdenJpaRepository ordenRepository;
    private final VentasApi ventasApi;
    private final ApplicationEventPublisher eventPublisher;
    private final RabbitTemplate rabbitTemplate;

    public OrdenService(OrdenJpaRepository ordenRepository, VentasApi ventasApi,
            ApplicationEventPublisher eventPublisher, RabbitTemplate rabbitTemplate) {
        this.ordenRepository = ordenRepository;
        this.ventasApi = ventasApi;
        this.eventPublisher = eventPublisher;
        this.rabbitTemplate = rabbitTemplate;
    }

    // ── Métodos públicos (contrato de OrdenesApi) ─────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public OrdenResumen obtenerOrden(UUID ordenId) {
        return OrdenResumen.desde(buscarPorId(ordenId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenResumen> listarOrdenes() {
        return todasLasOrdenes().stream()
                .map(OrdenResumen::desde)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DatosResumen> listarDatos() {
        return todasLasOrdenes().stream()
                .map(this::mapearDatosResumen)
                .toList();
    }

    @Override
    @Transactional
    public OrdenResumen crear(UUID clienteId, DireccionEnvio direccionEnvio) {
        Orden orden = new Orden(clienteId, direccionEnvio);
        Orden guardada = ordenRepository.save(orden);

        // Crear el evento UNA sola vez
        ProductoCompradoEvent event = new ProductoCompradoEvent(
                UUID.randomUUID(),
                Instant.now(),
                guardada.getId().valor(),
                clienteId,
                guardada.getItems().stream()
                        .map(item -> new ProductoCompradoEvent.ItemComprado(
                                item.getProductoId(),
                                item.getSku(),
                                item.getCantidad(),
                                item.getPrecioUnitario().getCantidad(),
                                item.getPrecioUnitario().getMoneda()))
                        .toList());

        // Evento interno (Spring)
        eventPublisher.publishEvent(event);

        // Evento externo (RabbitMQ)
        rabbitTemplate.convertAndSend(
                RabbitConfig.EVENTS_EXCHANGE,
                RabbitConfig.RK_PRODUCTO_COMPRADO,
                event);

        return OrdenResumen.desde(guardada);
    }

        /**eventPublisher.publishEvent(new ProductoCompradoEvent(
                UUID.randomUUID(),
                Instant.now(),
                guardada.getId().valor(),
                clienteId,
                guardada.getItems().stream()
                        .map(item -> new ProductoCompradoEvent.ItemComprado(
                                item.getProductoId(),
                                item.getSku(),
                                item.getCantidad(),
                                item.getPrecioUnitario().getCantidad(),
                                item.getPrecioUnitario().getMoneda()))
                        .toList()));

        // Publicar evento via RabbitMQ
        rabbitTemplate.convertAndSend(
                RabbitConfig.EVENTS_EXCHANGE,
                RabbitConfig.RK_PRODUCTO_COMPRADO,
                eventPublisher);

        return OrdenResumen.desde(guardada);
    }*/

    @Override
    @Transactional
    public OrdenResumen crearDesdeCarrito(UUID carritoId, DireccionEnvio direccionEnvio) {
        // 1. Obtener el resumen del carrito a través de la API pública de Ventas
        CarritoResumen carrito = ventasApi.obtenerResumen(carritoId);

        // 2. Construir la Orden con los datos del carrito
        Orden orden = new Orden(carrito.clienteId().getValor(), direccionEnvio);

        for (CarritoResumen.ItemCarritoResumen itemCarrito : carrito.items()) {
            ItemOrden itemOrden = ItemOrden.crear(
                    itemCarrito.productoId().valor(),
                    itemCarrito.nombreProducto(),
                    itemCarrito.sku(),
                    itemCarrito.cantidad(),
                    itemCarrito.precioUnitario());
            orden.agregarItem(itemOrden);
        }

        // 3. Guardar la orden
        Orden guardada = ordenRepository.save(orden);

        // 4. Publicar evento para que Ventas complete el checkout
        eventPublisher.publishEvent(new OrdenCreadaEvent(
                UUID.randomUUID(),
                Instant.now(),
                guardada.getId().valor(),
                carritoId,
                carrito.clienteId().getValor()));

        // Crear evento de producto comprado
        ProductoCompradoEvent event = new ProductoCompradoEvent(
                UUID.randomUUID(),
                Instant.now(),
                guardada.getId().valor(),
                guardada.getClienteId(),
                guardada.getItems().stream()
                        .map(item -> new ProductoCompradoEvent.ItemComprado(
                                item.getProductoId(),
                                item.getSku(),
                                item.getCantidad(),
                                item.getPrecioUnitario().getCantidad(),
                                item.getPrecioUnitario().getMoneda()))
                        .toList());

        // Evento interno
        eventPublisher.publishEvent(event);

        // Evento RabbitMQ (CORREGIDO)
        rabbitTemplate.convertAndSend(
                RabbitConfig.EVENTS_EXCHANGE,
                RabbitConfig.RK_PRODUCTO_COMPRADO,
                event);

        return OrdenResumen.desde(guardada);
    }

        /**eventPublisher.publishEvent(new ProductoCompradoEvent(
                UUID.randomUUID(),
                Instant.now(),
                guardada.getId().valor(),
                guardada.getClienteId(),
                guardada.getItems().stream()
                        .map(item -> new ProductoCompradoEvent.ItemComprado(
                                item.getProductoId(),
                                item.getSku(),
                                item.getCantidad(),
                                item.getPrecioUnitario().getCantidad(),
                                item.getPrecioUnitario().getMoneda()))
                        .toList()));

        // 6. Publicaer evento via RabbitMQ
        rabbitTemplate.convertAndSend(
                RabbitConfig.EVENTS_EXCHANGE,
                RabbitConfig.RK_PRODUCTO_COMPRADO,
                eventPublisher);

        return OrdenResumen.desde(guardada);
    }*/

    @Override
    @Transactional
    public OrdenResumen confirmar(UUID ordenId) {
        Orden orden = buscarPorId(ordenId);
        orden.confirmar();
        return OrdenResumen.desde(ordenRepository.save(orden));
    }

    @Override
    @Transactional
    public OrdenResumen procesarPago(UUID ordenId, String referenciaPago) {
        Orden orden = buscarPorId(ordenId);
        orden.procesarPago(referenciaPago);
        return OrdenResumen.desde(ordenRepository.save(orden));
    }

    @Override
    @Transactional
    public OrdenResumen marcarEnProceso(UUID ordenId) {
        Orden orden = buscarPorId(ordenId);
        orden.marcarEnProceso();
        return OrdenResumen.desde(ordenRepository.save(orden));
    }

    @Override
    @Transactional
    public OrdenResumen marcarEnviada(UUID ordenId, String numeroGuia) {
        Orden orden = buscarPorId(ordenId);
        orden.marcarEnviada(numeroGuia);
        return OrdenResumen.desde(ordenRepository.save(orden));
    }

    @Override
    @Transactional
    public OrdenResumen marcarEntregada(UUID ordenId) {
        Orden orden = buscarPorId(ordenId);
        orden.marcarEntregada();
        return OrdenResumen.desde(ordenRepository.save(orden));
    }

    @Override
    @Transactional
    public OrdenResumen cancelar(UUID ordenId, String motivo) {
        Orden orden = buscarPorId(ordenId);
        orden.cancelar(motivo);
        return OrdenResumen.desde(ordenRepository.save(orden));
    }

    // ── Métodos internos (package-private – solo visibles dentro del package) ──

    /**
     * Busca la entidad Orden por su UUID.
     * Uso interno; los callers externos reciben únicamente el DTO OrdenResumen.
     */
    @Transactional(readOnly = true)
    Orden buscarPorId(UUID ordenId) {
        return ordenRepository.findById(ordenId)
                .orElseThrow(() -> new DomainException("Orden no encontrada con id: " + ordenId));
    }

    /** Devuelve todas las órdenes como entidades de dominio (uso interno). */
    @Transactional(readOnly = true)
    List<Orden> todasLasOrdenes() {
        return ordenRepository.findAll();
    }

    // ── Mappers privados ──────────────────────────────────────────────────────

    private DatosResumen mapearDatosResumen(Orden orden) {
        DireccionEnvio dir = orden.getDireccionEnvio();
        String fecha = orden.getFechaCreacion() != null
                ? orden.getFechaCreacion().toLocalDate().toString()
                : "";
        String hora = orden.getFechaCreacion() != null
                ? orden.getFechaCreacion().toLocalTime().toString()
                : "";
        String direccion = dir != null ? dir.toString() : "";
        String telefono = dir != null ? dir.getTelefonoContacto() : "";
        String estadoDir = dir != null ? dir.getEstado() : "";

        String metodoPago = "";
        String formaPago = "";
        if (orden.getResumenPago() != null) {
            metodoPago = orden.getResumenPago().getMetodoPago() != null
                    ? orden.getResumenPago().getMetodoPago()
                    : "";
            formaPago = orden.getResumenPago().getReferenciaExterna() != null
                    ? orden.getResumenPago().getReferenciaExterna()
                    : "";
        }

        return new DatosResumen(
                orden.getClienteId(),
                "", // nombre – no disponible en la entidad Orden
                "", // apellido – no disponible en la entidad Orden
                orden.getEstado().name(),
                direccion,
                telefono,
                metodoPago,
                formaPago,
                fecha,
                hora);
    }
}
