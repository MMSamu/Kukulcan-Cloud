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

    private final CarritoJpaRepository carritoRepository;

    public CarritoService(CarritoJpaRepository carritoRepository) {
        this.carritoRepository = carritoRepository;
    }


    @Transactional
    public Carrito crear(UUID clienteId) {
        Carrito carrito = new Carrito(clienteId);
        return carritoRepository.save(carrito);
    }


    public Carrito obtenerCarrito(CarritoId carritoId) {
        return carritoRepository.findById(carritoId.value())
                .orElseThrow(() -> new DomainException("El carrito no existe"));
    }


    @Transactional
    public Carrito agregarProducto(CarritoId carritoId, UUID productoId, int cantidad, Money precio) {
        Carrito carrito = obtenerCarrito(carritoId);
        carrito.agregarProducto(productoId, cantidad, precio);
        return carritoRepository.save(carrito);
    }


    @Transactional
    public Carrito modificarCantidad(CarritoId carritoId, UUID productoId, int nuevaCantidad) {
        Carrito carrito = obtenerCarrito(carritoId);
        carrito.modificarCantidad(productoId, nuevaCantidad);
        return carritoRepository.save(carrito);
    }


    @Transactional
    public Carrito eliminarProducto(CarritoId carritoId, UUID productoId) {
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
}