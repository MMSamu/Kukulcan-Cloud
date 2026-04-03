package com.uamishop.orden.listener;

import com.uamishop.orden.service.OrdenService;
import com.uamishop.shared.event.CarritoFinalizadoEvent;
import com.uamishop.orden.domain.DireccionEnvio;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class CarritoFinalizadoListener {

    private final OrdenService ordenService;

    public CarritoFinalizadoListener(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @RabbitListener(queues = "uamishop.ventas.carrito.finalizado")
    public void handleCarritoFinalizado(CarritoFinalizadoEvent event) {
        // Construimos la dirección con los datos del evento
        DireccionEnvio direccion = DireccionEnvio.crear(
            event.calle() + " " + event.numero(),
            event.ciudad(),
            event.estado(),
            event.codigoPostal(),
            event.telefono()
        );

        // PASAMOS EL TOTAL REAL QUE VIENE EN EL EVENTO
        ordenService.crearDesdeCarrito(event.clienteId(), direccion, event.total());
        
        System.out.println("✅ Orden creada automáticamente vía RabbitMQ con monto: $" + event.total());
    }
}