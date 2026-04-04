package com.uamishop.orden.listener;

import com.uamishop.orden.config.RabbitConfig;
import com.uamishop.orden.controller.dto.ItemOrdenRequest;
import com.uamishop.orden.domain.DireccionEnvio;
import com.uamishop.orden.service.OrdenService;
import com.uamishop.shared.event.CarritoFinalizadoEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CarritoFinalizadoListener {

    private final OrdenService ordenService;

    public CarritoFinalizadoListener(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @RabbitListener(queues = RabbitConfig.CARRITO_FINALIZADO_QUEUE)
    public void manejarCarritoFinalizado(CarritoFinalizadoEvent evento) {
        // Nota: El record no tiene carritoId, usaremos clienteId o un log genérico
        System.out.println("📩 Evento recibido de RabbitMQ para cliente: " + evento.clienteId());

        // Reconstruimos la dirección desde los campos del evento
        DireccionEnvio direccion = null;
        if (evento.calle() != null) {
            direccion = DireccionEnvio.crear(
                evento.calle() + " " + evento.numero(),
                evento.ciudad(),
                evento.estado(),
                evento.codigoPostal(),
                evento.telefono()
            );
        }

        // Como el record que pasaste NO tiene la lista de items, 
        // pasamos una lista vacía para evitar errores de compilación.
        List<ItemOrdenRequest> itemsVacios = Collections.emptyList();

        // Llamamos al service. 
        // Usamos el 'evento.total()' para que el service no tenga que calcular nada (ya que no hay items)
        ordenService.registrarOActualizarMonto(
                null,           // No tenemos ID de orden en el evento
                evento.clienteId(), 
                direccion, 
                evento.total(), // Usamos el total que ya viene en el mensaje
                itemsVacios     // Lista vacía
        );
        
        System.out.println("✅ Orden pre-registrada exitosamente desde el evento con monto: " + evento.total());
    }
}