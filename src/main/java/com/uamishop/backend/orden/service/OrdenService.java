package com.uamishop.backend.orden.service;

import com.uamishop.backend.shared.exception.DomainException;
import com.uamishop.backend.ventas.domain.Carrito;
import com.uamishop.backend.ventas.domain.CarritoId;
import com.uamishop.backend.ventas.service.CarritoService;
import com.uamishop.backend.orden.api.DatosResumen;
import com.uamishop.backend.orden.api.OrdenesApi;
import com.uamishop.backend.orden.api.OrdenResumen;
import com.uamishop.backend.orden.domain.DireccionEnvio;
import com.uamishop.backend.orden.domain.ItemOrden;
import com.uamishop.backend.orden.domain.Orden;
import com.uamishop.backend.orden.repository.OrdenJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uamishop.backend.shared.event.ProductoCompradoEvent;
import org.springframework.context.ApplicationEventPublisher;

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
    private final CarritoService carritoService;
    private final ApplicationEventPublisher eventPublisher;

    public OrdenService(OrdenJpaRepository ordenRepository, CarritoService carritoService, ApplicationEventPublisher eventPublisher) {
        this.ordenRepository = ordenRepository;
        this.carritoService = carritoService;
        this.eventPublisher = eventPublisher;
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

        eventPublisher.publishEvent(new ProductoCompradoEvent(
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
                    item.getPrecioUnitario().getMoneda()
                )).toList()
        ));
        
        return OrdenResumen.desde(guardada);
    }

    @Override
    @Transactional
    public OrdenResumen crearDesdeCarrito(CarritoId carritoId, DireccionEnvio direccionEnvio) {
        // 1. Obtener el carrito
        Carrito carrito = carritoService.obtenerCarrito(carritoId);

        // 2. Construir la Orden con los datos del carrito
        Orden orden = new Orden(carrito.getClienteId().getValor(), direccionEnvio);

        for (com.uamishop.backend.ventas.domain.ItemCarrito itemCarrito : carrito.getItems()) {
            ItemOrden itemOrden = ItemOrden.crear(
                    itemCarrito.getProductoId().valor(),
                    itemCarrito.getNombreProducto(),
                    itemCarrito.getSku(),
                    itemCarrito.getCantidad(),
                    itemCarrito.getPrecioUnitario());
            orden.agregarItem(itemOrden);
        }

        // 3. Aplicar descuento si el carrito tenía uno
        if (carrito.getDescuento() != null && carrito.getDescuento().esPositivo()) {
            orden.aplicarDescuento(carrito.getDescuento());
        }

        // 4. Cerrar el carrito
        carrito.completarCheckout();

        Orden guardada = ordenRepository.save(orden);

        eventPublisher.publishEvent(new ProductoCompradoEvent(
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
                    item.getPrecioUnitario().getMoneda()
                )).toList()
        ));

        return OrdenResumen.desde(guardada);
    }

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
