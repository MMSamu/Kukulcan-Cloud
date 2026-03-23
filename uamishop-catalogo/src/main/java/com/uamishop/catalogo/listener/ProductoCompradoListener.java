package com.uamishop.catalogo.listener;

import com.uamishop.catalogo.service.ProductoEstadisticasService;
import com.uamishop.catalogo.shared.event.ProductoCompradoEvent;
import com.uamishop.catalogo.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
//import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

@Component
public class ProductoCompradoListener {

    private final ProductoEstadisticasService estadisticasService;

    public ProductoCompradoListener(ProductoEstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }


    @RabbitListener(queues = RabbitConfig.QUEUE_CATALOGO_PRODUCTO_COMPRADO)
    @Async // Listener ejecuta un hido distinto
    @Transactional(propagation = Propagation.REQUIRES_NEW) // Manejamos una subtransaccion
    // Si la tabla falla, la accion de agregar al carrito se mantiene funcionando
    public void onProductoComprado(ProductoCompradoEvent event) {
        event.items().forEach(item -> estadisticasService.registrarVenta(item.productoId(), item.cantidad()));
    }
}
