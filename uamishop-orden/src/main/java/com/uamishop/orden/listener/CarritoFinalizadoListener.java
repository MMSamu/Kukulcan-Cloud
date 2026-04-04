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
    System.out.println("📩 Evento recibido. Carrito ID: " + evento.carritoId());

    DireccionEnvio direccion = DireccionEnvio.crear(
        evento.calle() + " " + evento.numero(),
        evento.ciudad(),
        evento.estado(),
        evento.codigoPostal(),
        evento.telefono()
    );

    // PASAMOS EL carritoId para que la orden se guarde con ese ID
    ordenService.registrarOActualizarMonto(
            evento.carritoId(), // <--- AHORA SÍ HAY ID
            evento.clienteId(), 
            direccion, 
            evento.total(),     // Traemos el total de Ventas
            Collections.emptyList() 
    );
    
    System.out.println("✅ Orden pre-registrada con ID de carrito.");
}
}