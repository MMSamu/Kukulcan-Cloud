package com.uamishop.backend.catalogo.listener;

import com.uamishop.backend.catalogo.service.ProductoEstadisticasService;
import com.uamishop.backend.shared.event.ProductoCompradoEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

@Component
public class ProductoCompradoListener {

    private final ProductoEstadisticasService estadisticasService;

    public ProductoCompradoListener(ProductoEstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    @EventListener
    @Async // Listener ejecuta un hido distinto
    @Transactional(propagation = Propagation.REQUIRES_NEW) // Manejamos una subtransaccion
    // Si la tabla falla, la accion de agregar al carrito se mantiene funcionando
    public void onProductoComprado(ProductoCompradoEvent event) {
        event.items().forEach(item -> estadisticasService.registrarVenta(item.productoId(), item.cantidad()));
    }
}
