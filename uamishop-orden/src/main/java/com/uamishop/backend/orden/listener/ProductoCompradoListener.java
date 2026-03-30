package com.uamishop.backend.orden.listener;

import com.uamishop.backend.orden.config.RabbitConfig;
import com.uamishop.backend.shared.event.ProductoCompradoEvent;
import com.uamishop.backend.orden.service.OrdenService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
// Logger es para mostrar mensajes en consola
import org.slf4j.Logger;
// LoggerFactory es para crear el logger
import org.slf4j.LoggerFactory;

@Component
public class ProductoCompradoListener {

    // Logger funciona como un sistema de logs para mostrar mensajes en consola
    private static final Logger log = LoggerFactory.getLogger(ProductoCompradoListener.class);
    private final OrdenService ordenService;

    public ProductoCompradoListener(OrdenService ordenService) {
        this.ordenService = ordenService;
    }
    
    @RabbitListener(queues = RabbitConfig.QUEUE_CATALOGO_PRODUCTO_COMPRADO)
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onProductoComprado(ProductoCompradoEvent event) {
        log.info("Evento recibido: ProductoComprado para la Orden ID: {}", event.ordenId());

        try {
            // Usamos el eventId como referencia de pago,
            // ya que el record ProductoCompradoEvent no trae una referencia externa.
            String referencia = "EVENT-" + event.eventId().toString().substring(0, 8);

            // 1. Procesamos el pago en el dominio de Orden
            ordenService.procesarPago(event.ordenId(), referencia);

            // 2. Se pasa inmediatamente a "En Proceso"
            ordenService.marcarEnProceso(event.ordenId());

            log.info("Orden {} actualizada a estado PAGADA exitosamente.", event.ordenId());
        } catch (Exception e) {
            log.error("Error al procesar la compra de la orden {}: {}", event.ordenId(), e.getMessage());
            throw e;
        }
    }
}