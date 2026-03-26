package com.uamishop.ventas.service;

import com.uamishop.catalogo.api.CatalogoApi;
import com.uamishop.shared.domain.ClienteId;
import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.ProductoId;
import com.uamishop.shared.event.ProductoAgregadoAlCarritoEvent;
import com.uamishop.shared.exception.DomainException;
import com.uamishop.ventas.api.CarritoResumen;
import com.uamishop.ventas.api.VentasApi;
import com.uamishop.ventas.domain.Carrito;
import com.uamishop.ventas.domain.CarritoId;
import com.uamishop.ventas.repository.CarritoJpaRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class CarritoService implements VentasApi {

    // Toma el repositorio de Carrito para interactuar con la base de datos
    private final CarritoJpaRepository carritoRepository;
    //Enlace entre el CarritoService y Catalogo
    private final CatalogoApi catalogoApi;
    //Inyeccion de eventos
    private final ApplicationEventPublisher eventPublisher;

    // Constructor para inyectar el repositorio de Carrito y el API de Catálogo
    public CarritoService(CarritoJpaRepository carritoRepository, CatalogoApi catalogoApi, ApplicationEventPublisher eventPublisher) {
        this.carritoRepository = carritoRepository;
        this.catalogoApi = catalogoApi; //aquí se "habla" al otro servidor
        this.eventPublisher = eventPublisher;
    }

    // --- MÉTODOS PUBLICOS (Lo que ven otros servicios)---

    @Override
    @Transactional(readOnly = true)
    public CarritoResumen obtenerResumen(UUID carritoId) {
        // Usamos el método que ya tienes para obtener la entidad
        Carrito carrito = obtenerCarrito(new CarritoId(carritoId));
        // Mapea el CarritoResumen (el DTO público)
        return mapearCarritoResumen(carrito);
    }

    @Override
    @Transactional
    public void completarCheckoutPublico(UUID carritoId) { 
        // Convierte el UUID al Value Object del dominio
        CarritoId id = new CarritoId(carritoId);
        
        //Ejecuta la lógica directamente
        Carrito carrito = obtenerCarrito(id);
        carrito.completarCheckout();
        carritoRepository.save(carrito);
    }

    // --- MÉTODOS INTERNOS (Lógica de negocio de carrito) ---

    @Transactional // Maneja la transacción de forma automática
    public Carrito crear(ClienteId clienteId) {
        // Crea un nuevo carrito de compras para un cliente específico y lo guarda
        Carrito carrito = new Carrito(clienteId);
        // Guarda el carrito en la base de datos utilizando el repositorio y devuelve el carrito creado
        return carritoRepository.save(carrito);
    }

    // Método para obtener un carrito de compras por su ID
    public Carrito obtenerCarrito(CarritoId carritoId) {
        // Busca el carrito en la base de datos utilizando su ID
        // Si no se encuentra, lanza una excepción indicando que el carrito no existe
        return carritoRepository.findById(carritoId.value())
                .orElseThrow(() -> new DomainException("El carrito no existe"));
    }

    // Método para agregar un producto al carrito de compras
    // usando la API pública de Catálogo para el resumen
    @Transactional
    public Carrito agregarProducto(CarritoId carritoId, ProductoId productoId, int cantidad) {
        var productoResumen = catalogoApi.obtenerProducto(productoId.valor());
        // Valida la regla de negocio
        if (!productoResumen.disponible()) {
            throw new DomainException("El producto no está disponible en catálogo");
        }
        // El precio viene como un objeto Money desde el Shared Kernel)
        Money precioOficial = productoResumen.precio();
        // Persistencia
        Carrito carrito = obtenerCarrito(carritoId);
        carrito.agregarProducto(productoId, cantidad, precioOficial);

        Carrito guardado = carritoRepository.save(carrito);

        eventPublisher.publishEvent(new ProductoAgregadoAlCarritoEvent(
            UUID.randomUUID(),
            Instant.now(),
            productoId.valor(),
            carritoId.value(),
            cantidad,
            precioOficial.getCantidad(),
            precioOficial.getMoneda()
        ));
        
        return guardado;
    }

    // Método para modificar la cantidad de un producto en el carrito de compras
    @Transactional
    public Carrito modificarCantidad(CarritoId carritoId, ProductoId productoId, int nuevaCantidad) {
        Carrito carrito = obtenerCarrito(carritoId);
        // Modifica la cantidad del producto en el carrito utilizando el método
        // modificarCantidad del carrito
        carrito.modificarCantidad(productoId, nuevaCantidad);
        // Guarda el carrito actualizado en la base de datos y devuelve el carrito
        // modificado
        return carritoRepository.save(carrito);
    }

    // Método para eliminar un producto del carrito de compras
    @Transactional
    public Carrito eliminarProducto(CarritoId carritoId, ProductoId productoId) {
        Carrito carrito = obtenerCarrito(carritoId);
        // Elimina un producto del carrito utilizando el método eliminarProducto del carrito
        carrito.eliminarProducto(productoId);
        return carritoRepository.save(carrito);
    }

    // Método para vaciar el carrito de compras, eliminando todos los productos del mismo
    @Transactional
    public Carrito vaciar(CarritoId carritoId) {
        Carrito carrito = obtenerCarrito(carritoId);
        carrito.vaciar();
        return carritoRepository.save(carrito);
    }

    // Método para iniciar el proceso de checkout del carrito de compras
    // Cambia el estado del carrito a "en proceso de checkout"
    @Transactional
    public Carrito iniciarCheckout(CarritoId carritoId) {
        Carrito carrito = obtenerCarrito(carritoId);
        carrito.iniciarCheckout();
        return carritoRepository.save(carrito);
    }

    // Método para completar el proceso de checkout del carrito de compras
    @Transactional
    public Carrito completarCheckout(CarritoId carritoId) {
        Carrito carrito = obtenerCarrito(carritoId);
        // Completa el proceso de checkout del carrito utilizando el método completarCheckout del carrito
        // lo que cambia su estado a "completado"
        carrito.completarCheckout();
        return carritoRepository.save(carrito);
    }

    // Método para abandonar el carrito de compras
    @Transactional
    public Carrito abandonar(CarritoId carritoId) {
        // Obtiene el carrito de compras utilizando su ID
        Carrito carrito = obtenerCarrito(carritoId);
        carrito.abandonar();
        return carritoRepository.save(carrito);
    }

    // --- MAPPER PRIVADO (Lo que ve el propio servicio) ---

    /* Mapea un carrito a su representación resumida */
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