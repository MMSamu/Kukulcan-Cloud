package com.uamishop.orden.service;

import com.uamishop.shared.exception.DomainException;
import com.uamishop.orden.config.RabbitConfig;
import com.uamishop.orden.controller.dto.DatosResumen;
import com.uamishop.orden.controller.dto.ItemOrdenRequest;
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
    private final RabbitTemplate rabbitTemplate;

    public OrdenService(OrdenJpaRepository ordenRepository, 
                        RabbitTemplate rabbitTemplate) {
        this.ordenRepository = ordenRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

@Transactional
public OrdenResumen registrarOActualizarMonto(UUID ordenId, UUID clienteId, DireccionEnvio direccionEnvio, BigDecimal monto) {
    Orden orden;

    // Si ordenId es null (como viene del Listener), creamos una nueva directamente
    if (ordenId == null) {
        orden = new Orden(clienteId, direccionEnvio);
        System.out.println("🌱 Nueva orden generada internamente: " + orden.getId().valor());
    } else {
        // Si no es null, intentamos buscarla o crearla si no existe
        orden = ordenRepository.findById(ordenId)
                .orElseGet(() -> new Orden(clienteId, direccionEnvio));
    }

    if (monto != null) {
        orden.setTotal(monto);
        orden.setSubtotal(monto);
    }

    Orden guardada = ordenRepository.save(orden);
    notificarOrdenCreada(guardada, clienteId);
    
    return OrdenResumen.desde(guardada);
}

@Transactional
public OrdenResumen crear(UUID clienteId, DireccionEnvio direccionEnvio, BigDecimal monto, List<ItemOrdenRequest> items) {
    // 1. Crear la instancia de Orden
    Orden orden = new Orden(clienteId, direccionEnvio);
    
    // 2. Seteamos montos iniciales (Money se encarga internamente en la entidad)
    orden.setTotal(monto != null ? monto : BigDecimal.ZERO);
    orden.setSubtotal(monto != null ? monto : BigDecimal.ZERO);
    
    // NOTA: Si en el futuro quieres procesar los 'items', aquí es donde 
    // convertirías los ItemOrdenRequest a la entidad ItemOrden.

    Orden guardada = ordenRepository.save(orden);
    notificarOrdenCreada(guardada, clienteId);
    
    return OrdenResumen.desde(guardada);
}

    @Transactional(readOnly = true)
    public OrdenResumen obtenerOrden(UUID ordenId) {
        return OrdenResumen.desde(buscarPorId(ordenId));
    }

    @Transactional(readOnly = true)
    public List<OrdenResumen> listarOrdenes() {
        return ordenRepository.findAll().stream()
                .map(OrdenResumen::desde)
                .toList();
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
        // En tu Entidad, marcarEnProceso() requiere que esté CONFIRMADA.
        // Si ya se pagó, el estado es PREPARACION. 
        orden.marcarEnProceso();
        return OrdenResumen.desde(ordenRepository.save(orden));
    }

    @Transactional
    public OrdenResumen marcarEnviada(UUID ordenId) {
        Orden orden = buscarPorId(ordenId);
        DireccionEnvio destino = orden.getDireccionEnvio();
        
        // Validación de seguridad para evitar el NullPointerException
        if (destino == null || destino.getEstado() == null) {
            throw new DomainException("La orden no tiene una dirección de envío válida (Estado/Provincia faltante).");
        }

        String prefijo = destino.getEstado().length() >= 3 
                ? destino.getEstado().substring(0, 3).toUpperCase() 
                : "GEN";
        
        String guia = "ENV-" + prefijo + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        orden.marcarEnviada(guia);
        
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

    @Transactional
    public void actualizarMontoOrden(UUID ordenId, BigDecimal monto) {
        Orden orden = buscarPorId(ordenId);
        orden.setTotal(monto);
        orden.setSubtotal(monto);
        ordenRepository.save(orden);
    }

    private Orden buscarPorId(UUID id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new DomainException("Orden no encontrada: " + id));
    }

    private void notificarOrdenCreada(Orden guardada, UUID clienteId) {
        OrdenCreadaEvent evento = new OrdenCreadaEvent(
                UUID.randomUUID(), Instant.now(), guardada.getId().valor(), clienteId, null
        );
        rabbitTemplate.convertAndSend(RabbitConfig.EVENTS_EXCHANGE, RabbitConfig.RK_ORDEN_CREADA, evento);
    }
}