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
public void handleCarritoFinalizado(CarritoFinalizadoEvent event) {
    // Si el evento trae 'calle' y 'numero' por separado, júntalos:
    String calleCompleta = event.calle() + " " + event.numero();

    DireccionEnvio direccion = DireccionEnvio.crear(
            calleCompleta,      // calle
            event.ciudad(),     // ciudad
            event.estado(),     // estado
            event.codigoPostal(),// codigoPostal
            event.telefono()    // telefonoContacto (debe ser de 10 dígitos)
    );

    ordenService.registrarOActualizarMonto(
            null, 
            event.clienteId(), 
            direccion, 
            event.total() 
    );
}
}