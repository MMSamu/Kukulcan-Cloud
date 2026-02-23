package com.uamishop.backend.ventas.service;

import com.uamishop.backend.shared.domain.Money;
import com.uamishop.backend.shared.exception.DomainException;
import com.uamishop.backend.ventas.domain.Carrito;
import com.uamishop.backend.ventas.domain.CarritoId;
import com.uamishop.backend.ventas.repository.CarritoJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CarritoService {

    // Inyecta el repositorio de Carrito para interactuar con la base de datos
    private final CarritoJpaRepository carritoRepository;

    // Constructor para inyectar el repositorio de Carrito
    public CarritoService(CarritoJpaRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }

    @Transactional // Permite manejar la transacción de forma automática
    public Carrito crear(UUID clienteId) {
        // Crea un nuevo carrito de compras para un cliente específico y lo guarda en la
        // base de datos
        Carrito carrito = new Carrito(clienteId);
        // Guarda el carrito en la base de datos utilizando el repositorio y devuelve el
        // carrito creado
        return carritoRepository.save(carrito);
    }

    // Método para obtener un carrito de compras por su ID
    public Carrito obtenerCarrito(CarritoId carritoId) {
        // Busca el carrito en la base de datos utilizando su ID.
        // Si no se encuentra, lanza una excepción indicando que el carrito no existe.
        return carritoRepository.findById(carritoId.value())
                .orElseThrow(() -> new DomainException("El carrito no existe"));
    }

    // Método para agregar un producto al carrito de compras
    @Transactional
    public Carrito agregarProducto(CarritoId carritoId, UUID productoId, int cantidad, Money precio) {
        Carrito carrito = obtenerCarrito(carritoId);
        // Agrega un producto al carrito de compras utilizando el método agregarProducto
        // del carrito.
        carrito.agregarProducto(productoId, cantidad, precio);
        // Guarda el carrito actualizado en la base de datos y devuelve el carrito
        // modificado
        return carritoRepository.save(carrito);
    }

    // Método para modificar la cantidad de un producto en el carrito de compras
    @Transactional
    public Carrito modificarCantidad(CarritoId carritoId, UUID productoId, int nuevaCantidad) {
        Carrito carrito = obtenerCarrito(carritoId);
        // Modifica la cantidad del producto en el carrito utilizando el método
        // modificarCantidad del carrito.
        carrito.modificarCantidad(productoId, nuevaCantidad);
        // Guarda el carrito actualizado en la base de datos y devuelve el carrito
        // modificado
        return carritoRepository.save(carrito);
    }

    // Método para eliminar un producto del carrito de compras
    @Transactional
    public Carrito eliminarProducto(CarritoId carritoId, UUID productoId) {
        Carrito carrito = obtenerCarrito(carritoId);
        // Elimina un producto del carrito utilizando el método eliminarProducto del
        // carrito.
        carrito.eliminarProducto(productoId);
        return carritoRepository.save(carrito);
    }

    // Método para vaciar el carrito de compras, eliminando todos los productos del
    // mismo
    @Transactional
    public Carrito vaciar(CarritoId carritoId) {
        Carrito carrito = obtenerCarrito(carritoId);
        carrito.vaciar();
        return carritoRepository.save(carrito);
    }

    // Método para iniciar el proceso de checkout del carrito de compras.
    // Cambia el estado del carrito a "en proceso de checkout".
    @Transactional
    public Carrito iniciarCheckout(CarritoId carritoId) {
        Carrito carrito = obtenerCarrito(carritoId);
        carrito.iniciarCheckout();
        return carritoRepository.save(carrito);
    }

    // Método para completar el proceso de checkout del carrito de compras.
    @Transactional
    public Carrito completarCheckout(CarritoId carritoId) {
        Carrito carrito = obtenerCarrito(carritoId);
        // Completa el proceso de checkout del carrito utilizando el método
        // completarCheckout del carrito,
        // lo que cambia su estado a "completado".
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
}