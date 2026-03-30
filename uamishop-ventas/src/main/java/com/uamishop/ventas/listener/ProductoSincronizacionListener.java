package com.uamishop.ventas.listener;

import com.uamishop.shared.domain.Money;
import com.uamishop.shared.event.ProductoActivadoEvent;
import com.uamishop.ventas.domain.Producto;
import com.uamishop.ventas.repository.ProductoJpaRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProductoSincronizacionListener {

    private final ProductoJpaRepository productoRepository;

    public ProductoSincronizacionListener(ProductoJpaRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @RabbitListener(queues = "ventas.producto.activado") 
    public void handleProductoActivado(ProductoActivadoEvent event) {
        Producto producto = new Producto(
            event.getProductoId(),     // Agrega "get" y usa paréntesis
            event.getNombre(),         // Agrega "get"
            event.getSku(),            // Agrega "get"
            new Money(event.getPrecioMonto(), event.getPrecioMoneda()) 
        );
        productoRepository.save(producto);
    }
}