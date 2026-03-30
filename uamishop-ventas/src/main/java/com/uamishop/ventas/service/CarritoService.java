package com.uamishop.ventas.service;

import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.ProductoId;
import com.uamishop.shared.event.ProductoAgregadoAlCarritoEvent;
import com.uamishop.shared.exception.DomainException;
import com.uamishop.ventas.api.CarritoResumen;
import com.uamishop.ventas.domain.Carrito;
import com.uamishop.ventas.domain.CarritoId;
import com.uamishop.ventas.domain.Producto; 
import com.uamishop.ventas.repository.CarritoJpaRepository;
import com.uamishop.ventas.repository.ProductoJpaRepository; 
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class CarritoService {

    private final CarritoJpaRepository carritoRepository;
    private final ProductoJpaRepository productoRepository; 
    private final ApplicationEventPublisher eventPublisher;

    public CarritoService(CarritoJpaRepository carritoRepository, 
                          ProductoJpaRepository productoRepository, 
                          ApplicationEventPublisher eventPublisher) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
        this.eventPublisher = eventPublisher;
    }

    // --- MÉTODOS PÚBLICOS ---

    @Transactional(readOnly = true)
    public CarritoResumen obtenerResumen(UUID carritoId) {
        Carrito carrito = obtenerCarrito(new CarritoId(carritoId));
        return mapearCarritoResumen(carrito);
    }

    @Transactional
    public void completarCheckoutPublico(UUID carritoId) { 
        CarritoId id = new CarritoId(carritoId);
        Carrito carrito = obtenerCarrito(id);
        carrito.completarCheckout();
        carritoRepository.save(carrito);
    }

    @Transactional 
    public Carrito crear(ClienteId clienteId) {
        Carrito carrito = new Carrito(clienteId);
        return carritoRepository.save(carrito);
    }

    public Carrito obtenerCarrito(CarritoId carritoId) {
        return carritoRepository.findById(carritoId.value())
                .orElseThrow(() -> new DomainException("El carrito no existe"));
    }

    /**
     * MÉTODO CORREGIDO: 
     * Ahora busca el precio real en la tabla de productos local (sincronizada vía RabbitMQ).
     */
    @Transactional
    public Carrito agregarProducto(CarritoId carritoId, ProductoId productoId, int cantidad) {
        
        // 1. Buscamos el producto en nuestra DB local de Ventas
        // Si no existe, significa que RabbitMQ aún no nos avisa del Catálogo
        Producto productoLocal = productoRepository.findById(productoId.valor())
                .orElseThrow(() -> new DomainException("Error: El producto no ha sido sincronizado desde el Catálogo todavía."));

        // 2. Extraemos el precio real que llegó desde el Listener
        Money precioReal = productoLocal.getPrecio(); 

        // 3. Buscamos el carrito
        Carrito carrito = obtenerCarrito(carritoId);
        
        // 4. Agregamos el producto con su precio oficial
        carrito.agregarProducto(productoId, cantidad, precioReal);

        // 5. Guardamos cambios
        Carrito guardado = carritoRepository.save(carrito);

        // 6. Notificamos al resto del sistema
        eventPublisher.publishEvent(new ProductoAgregadoAlCarritoEvent(
            UUID.randomUUID(),
            Instant.now(),
            productoId.valor(),
            carritoId.value(),
            cantidad,
            precioReal.getCantidad(),
            precioReal.getMoneda()
        ));
        
        return guardado;
    }

    @Transactional
    public Carrito modificarCantidad(CarritoId carritoId, ProductoId productoId, int nuevaCantidad) {
        Carrito carrito = obtenerCarrito(carritoId);
        carrito.modificarCantidad(productoId, nuevaCantidad);
        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito eliminarProducto(CarritoId carritoId, ProductoId productoId) {
        Carrito carrito = obtenerCarrito(carritoId);
        carrito.eliminarProducto(productoId);
        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito vaciar(CarritoId carritoId) {
        Carrito carrito = obtenerCarrito(carritoId);
        carrito.vaciar();
        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito iniciarCheckout(CarritoId carritoId) {
        Carrito carrito = obtenerCarrito(carritoId);
        carrito.iniciarCheckout();
        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito completarCheckout(CarritoId carritoId) {
        Carrito carrito = obtenerCarrito(carritoId);
        carrito.completarCheckout();
        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito abandonar(CarritoId carritoId) {
        Carrito carrito = obtenerCarrito(carritoId);
        carrito.abandonar();
        return carritoRepository.save(carrito);
    }

    // --- MAPPER PRIVADO ---

    private CarritoResumen mapearCarritoResumen(Carrito carrito) {
        return new CarritoResumen(
            carrito.getId().value(),
            carrito.getClienteId(), 
            carrito.getEstado().name(),
            carrito.getItems().stream()
                .map(item -> new CarritoResumen.ItemCarritoResumen(
                    item.getProductoId(), 
                    item.getNombreProducto(),
                    item.getSku(),
                    item.getCantidad(),
                    item.getPrecioUnitario() 
                ))
                .toList()
        );
    }
}