package com.uamishop.backend.catalogo.listener;

import com.uamishop.backend.catalogo.service.ProductoEstadisticasService;
import com.uamishop.backend.shared.event.ProductoAgregadoAlCarritoEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

@Component
public class ProductoAgregadoAlCarritoListener {

    private final ProductoEstadisticasService estadisticasService;

    public ProductoAgregadoAlCarritoListener(ProductoEstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    @EventListener
    @Async // Listener ejecuta un hido distinto
    @Transactional(propagation = Propagation.REQUIRES_NEW) // Manejamos una transaccion nueva
    public void onProductoAgregadoAlCarrito(ProductoAgregadoAlCarritoEvent event) {
        estadisticasService.registrarVenta(event.productoId(), event.cantidad());
    }
}
