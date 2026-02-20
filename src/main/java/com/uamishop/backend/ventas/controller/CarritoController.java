package com.uamishop.backend.ventas.controller;

import com.uamishop.backend.shared.domain.Money;
import com.uamishop.backend.ventas.controller.dto.AgregarProductoRequest;
import com.uamishop.backend.ventas.controller.dto.CarritoMapper;
import com.uamishop.backend.ventas.controller.dto.CarritoRequest;
import com.uamishop.backend.ventas.controller.dto.CarritoResponseDTO;
import com.uamishop.backend.ventas.domain.*;
import com.uamishop.backend.ventas.service.CarritoService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

/* Este controlador maneja las solicitudes relacionadas con los carritos de compra */
@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    private final CarritoService service;

    // Inyección de dependencias a través del constructor
    public CarritoController(CarritoService service) {
        this.service = service;
    }

    // Endpoint para crear un nuevo carrito. Recibe el ID del cliente y devuelve el carrito creado.
    @PostMapping
    public ResponseEntity<CarritoResponseDTO> crear(@Valid @RequestBody CarritoRequest request) {
        Carrito carrito = service.crear(request.clienteId());
        return ResponseEntity.status(HttpStatus.CREATED).body(CarritoMapper.toDTO(carrito));
    }

    // Endpoint para obtener un carrito por su ID. Devuelve el carrito encontrado o un error si no existe.
    @GetMapping("/{id}")
    public ResponseEntity<CarritoResponseDTO> obtener(@PathVariable UUID id) {
        Carrito carrito = service.obtenerCarrito(new CarritoId(id));
        return ResponseEntity.ok(CarritoMapper.toDTO(carrito));
    }

    // Endpoint para agregar un producto al carrito. Recibe el ID del carrito, el ID del producto, 
    // la cantidad y el precio.
    @PostMapping("/{id}/productos")
    public ResponseEntity<CarritoResponseDTO> agregar(@PathVariable UUID id, @Valid @RequestBody AgregarProductoRequest request) {
        Money precioDominio = Money.pesos(request.precioMonto().doubleValue());
        Carrito carrito = service.agregarProducto(new CarritoId(id),request.productoId(), request.cantidad(), precioDominio);       
        return ResponseEntity.ok(CarritoMapper.toDTO(carrito));
    }

    // Endpoint para modificar la cantidad de un producto en el carrito. 
    // Recibe el ID del carrito, el ID del producto y la nueva cantidad.
    @PatchMapping("/{id}/productos/{pId}")
    public ResponseEntity<CarritoResponseDTO> modificar(@PathVariable UUID id, @PathVariable UUID pId, @Valid @RequestBody AgregarProductoRequest request) {
        Carrito carrito = service.modificarCantidad(new CarritoId(id), pId, request.cantidad());
        return ResponseEntity.ok(CarritoMapper.toDTO(carrito));
    }

    // Endpoint para eliminar un producto del carrito. Recibe el ID del carrito y el ID del producto a eliminar.
    @DeleteMapping("/{id}/productos/{pId}")
    public ResponseEntity<CarritoResponseDTO> eliminar(@PathVariable UUID id, @PathVariable UUID pId) {
        Carrito carrito = service.eliminarProducto(new CarritoId(id), pId);
        return ResponseEntity.ok(CarritoMapper.toDTO(carrito));
    }

    // Endpoint para vaciar el carrito, eliminando todos los productos. Recibe el ID del carrito.
    @DeleteMapping("/{id}/productos")
    public ResponseEntity<Void> vaciar(@PathVariable UUID id) {
        service.vaciar(new CarritoId(id));
        return ResponseEntity.noContent().build();
    }

    // Endpoint para iniciar el proceso de checkout del carrito. Recibe el ID del carrito 
    // y devuelve el carrito actualizado.
    @PostMapping("/{id}/checkout")
    public ResponseEntity<CarritoResponseDTO> checkout(@PathVariable UUID id) {
        Carrito carrito = service.iniciarCheckout(new CarritoId(id));
        return ResponseEntity.ok(CarritoMapper.toDTO(carrito));
    }

    // Endpoint para completar el proceso de checkout del carrito. 
    // Recibe el ID del carrito y devuelve el carrito actualizado.
    @PostMapping("/{id}/completar")
    public ResponseEntity<CarritoResponseDTO> completar(@PathVariable UUID id) {
        Carrito carrito = service.completarCheckout(new CarritoId(id));
        return ResponseEntity.ok(CarritoMapper.toDTO(carrito));
    }

    // Endpoint para abandonar el carrito, cancelando el proceso de compra. 
    // Recibe el ID del carrito y devuelve el carrito actualizado.
    @PostMapping("/{id}/abandonar")
    public ResponseEntity<CarritoResponseDTO> abandonar(@PathVariable UUID id) {
        Carrito carrito = service.abandonar(new CarritoId(id));
        return ResponseEntity.ok(CarritoMapper.toDTO(carrito));
    }
}