package com.uamishop.orden.listener;

import com.uamishop.orden.service.OrdenService;
import com.uamishop.shared.event.CarritoFinalizadoEvent;
import com.uamishop.orden.domain.DireccionEnvio;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.uamishop.orden.config.RabbitConfig;

@Component
public class CarritoFinalizadoListener {

    private final OrdenService ordenService;

    public CarritoFinalizadoListener(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

@RabbitListener(queues = RabbitConfig.QUEUE_CARRITO_FINALIZADO)
public void handlesCarritoFinalizado(CarritoFinalizadoEvent event) {
    // 1. Construir la calle
    String calleCompleta = event.calle() + " " + event.numero();

    // 2. Crear el objeto de dominio DireccionEnvio
    DireccionEnvio direccion = DireccionEnvio.crear(
            calleCompleta,
            event.ciudad(),
            event.estado(),
            event.codigoPostal(),
            event.telefono()
    );

    // 3. Llamar al service. 
    // Pasamos NULL en el primer parámetro porque CarritoFinalizadoEvent no tiene ordenId.
    // El Service se encargará de generar uno nuevo.
    ordenService.registrarOActualizarMonto(
            null, 
            event.clienteId(), 
            direccion, 
            event.total() 
    );
}
}