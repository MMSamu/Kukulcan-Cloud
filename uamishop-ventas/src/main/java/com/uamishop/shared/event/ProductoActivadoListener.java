package com.uamishop.shared.event;

import com.uamishop.shared.domain.Money;
import com.uamishop.ventas.domain.Producto;
import com.uamishop.ventas.repository.ProductoJpaRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProductoActivadoListener {

    private final ProductoJpaRepository productoRepository;

    public ProductoActivadoListener(ProductoJpaRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @RabbitListener(queues = "ventas.producto.activado")
    public void handleProductoActivado(ProductoActivadoEvent event) {
        System.out.println("Ventas recibió producto: " + event.getNombre());
        
        // Creamos el dinero usando el método de fábrica 'pesos'
        Money precio = Money.pesos(event.getPrecioMonto().doubleValue());
        
        // Guardamos en nuestra base de datos local de Ventas
        Producto nuevoProducto = new Producto(
            event.getProductoId(),
            event.getNombre(),
            event.getSku(),
            precio
        );
        
        productoRepository.save(nuevoProducto);
    }
}