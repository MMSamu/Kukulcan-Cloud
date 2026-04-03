package com.uamishop.ventas.service;

import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.ProductoId;
import com.uamishop.shared.event.CarritoFinalizadoEvent;
import com.uamishop.shared.exception.DomainException;
import com.uamishop.ventas.api.CarritoResumen;
import com.uamishop.ventas.config.RabbitConfig;
import com.uamishop.ventas.domain.Carrito;
import com.uamishop.ventas.domain.CarritoId;
import com.uamishop.catalogo.api.CatalogoApi;
import com.uamishop.catalogo.api.ProductoResumen;
import com.uamishop.ventas.repository.CarritoJpaRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Instant;
import java.util.UUID;

@Service
public class CarritoService {

    private final CarritoJpaRepository carritoRepository;
    private final CatalogoApi catalogoApi;
    private final RabbitTemplate rabbitTemplate; // <--- AGREGAR

    public CarritoService(CarritoJpaRepository carritoRepository, 
                          CatalogoApi catalogoApi, 
                          RabbitTemplate rabbitTemplate) { // <--- AGREGAR
        this.carritoRepository = carritoRepository;
        this.catalogoApi = catalogoApi;
        this.rabbitTemplate = rabbitTemplate;
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
        ProductoResumen productoExterno = catalogoApi.obtenerProducto(productoId.valor());
        
        if (productoExterno == null) {
            throw new DomainException("El producto no existe en el catálogo");
        }

        Carrito carrito = obtenerCarrito(carritoId);
        
        // AHORA SÍ LE PASAMOS EL NOMBRE Y EL SKU QUE VIENEN DEL CATÁLOGO
        carrito.agregarProducto(
            productoId, 
            cantidad, 
            productoExterno.precio(), 
            productoExterno.nombre(), 
            "S/N" // O productoExterno.sku() si existe
        );

        return carritoRepository.save(carrito);
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
        
        Carrito carritoGuardado = carritoRepository.save(carrito);

        try {
            // Creamos el evento incluyendo el TOTAL real del carrito
            CarritoFinalizadoEvent evento = new CarritoFinalizadoEvent(
                carrito.getClienteId().getValor(),
                "Av. San Rafael Atlixco", // O los datos que tengas en tu form de checkout
                "186", 
                "Iztapalapa", 
                "CDMX", 
                "09340", 
                "555-1234",
                carrito.getTotal().getCantidad() // <--- EL MONTO REAL AQUÍ
            );

            rabbitTemplate.convertAndSend(
                RabbitConfig.EVENTS_EXCHANGE, 
                "uamishop.ventas.carrito.finalizado", 
                evento
            );
            
            System.out.println("🚀 Evento de checkout enviado a Órdenes con total: $" + carrito.getTotal().getCantidad());
        } catch (Exception e) {
            System.err.println("❌ Falló el envío del evento: " + e.getMessage());
        }

        return carritoGuardado;
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