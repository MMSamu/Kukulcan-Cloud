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

    // --- MÉTODO PUENTE (SWAGGER + RABBITMQ) ---
    @Transactional
    public OrdenResumen registrarOActualizarMonto(UUID ordenId, UUID clienteId, DireccionEnvio direccionEnvio, BigDecimal monto) {
        // Buscamos si ya existe una orden para este cliente
        List<Orden> ordenesDelCliente = ordenRepository.findAll().stream()
                .filter(o -> o.getClienteId().equals(clienteId))
                .sorted((o1, o2) -> o2.getFechaCreacion().compareTo(o1.getFechaCreacion())) // La más reciente
                .toList();

        Orden orden;
        if (!ordenesDelCliente.isEmpty()) {
            orden = ordenesDelCliente.get(0); // Usamos la existente
        } else {
            orden = new Orden(clienteId, direccionEnvio); // Creamos una nueva
        }

        // Si RabbitMQ envía el monto, se lo inyectamos
        if (monto != null) {
            orden.setTotal(monto);
            orden.setSubtotal(monto);
        } else if (orden.getTotal() == null) {
            // Si viene de Swagger y no tiene monto, iniciamos en 0
            orden.setTotal(BigDecimal.ZERO);
            orden.setSubtotal(BigDecimal.ZERO);
        }

        Orden guardada = ordenRepository.save(orden);
        
        // Notificamos solo si es nueva
        if (ordenesDelCliente.isEmpty()) {
            notificarOrdenCreada(guardada, clienteId);
        }

        return OrdenResumen.desde(guardada);
    }
    // ------------------------------------------

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
        orden.setTotal(monto);
        orden.setSubtotal(monto);
        
        Orden guardada = ordenRepository.save(orden);
        notificarOrdenCreada(guardada, clienteId);
        return OrdenResumen.desde(guardada);
    }

    @Transactional
    public OrdenResumen crearDesdeCarrito(UUID clienteId, DireccionEnvio direccionEnvio, BigDecimal monto) {
        return crear(clienteId, direccionEnvio, monto);
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
    public OrdenResumen marcarEnviada(UUID ordenId) {
        // 1. Buscamos la orden (que ya trae la dirección que le pasaste desde el carrito)
        Orden orden = buscarPorId(ordenId);
        
        // 2. Extraemos la dirección guardada para usarla en la lógica (opcional, 
        // por ejemplo, si el CP determina las letras de la guía)
        DireccionEnvio destino = orden.getDireccionEnvio();
        String prefijoEstado = destino.getEstado().substring(0, 3).toUpperCase(); // ej: "CDM" para CDMX
        
        // 3. Generamos la guía usando los datos reales de envío
        String numeroGuiaGenerado = "ENV-" + prefijoEstado + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        // 4. Marcamos como enviada y le asignamos la guía
        orden.marcarEnviada(numeroGuiaGenerado);
        
        System.out.println("📦 Orden enviada a " + destino.getCiudad() + ". Guía generada: " + numeroGuiaGenerado);
        
        // 5. Guardamos
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