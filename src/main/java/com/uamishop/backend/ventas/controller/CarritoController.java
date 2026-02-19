package com.uamishop.backend.ventas.controller;

import com.uamishop.backend.shared.domain.Money;
import com.uamishop.backend.ventas.domain.*;
import com.uamishop.backend.ventas.service.CarritoService;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    private final CarritoService service;

    public CarritoController(CarritoService service) {
        this.service = service;
    }

    @PostMapping
    public Carrito crear(UUID clienteId) {
        return service.crear(clienteId);
    }

    @GetMapping("/{id}")
    public Carrito obtener(@PathVariable UUID id) {
        return service.obtenerCarrito(new CarritoId(id));
    }

    @PostMapping("/{id}/productos")
    public Carrito agregar(@PathVariable UUID id, UUID productoId, int cantidad, @RequestBody Money precio) {
        return service.agregarProducto(new CarritoId(id), productoId, cantidad, precio);
    }

    @PatchMapping("/{id}/productos/{pId}")
    public Carrito modificar(@PathVariable UUID id, @PathVariable UUID pId, int cantidad) {
        return service.modificarCantidad(new CarritoId(id), pId, cantidad);
    }

    @DeleteMapping("/{id}/productos/{pId}")
    public Carrito eliminar(@PathVariable UUID id, @PathVariable UUID pId) {
        return service.eliminarProducto(new CarritoId(id), pId);
    }

    @DeleteMapping("/{id}/productos")
    public void vaciar(@PathVariable UUID id) {
        service.vaciar(new CarritoId(id));
    }

    @PostMapping("/{id}/checkout")
    public Carrito checkout(@PathVariable UUID id) {
        return service.iniciarCheckout(new CarritoId(id));
    }

    @PostMapping("/{id}/completar")
    public Carrito completar(@PathVariable UUID id) {
        return service.completarCheckout(new CarritoId(id));
    }

    @PostMapping("/{id}/abandonar")
    public Carrito abandonar(@PathVariable UUID id) {
        return service.abandonar(new CarritoId(id));
    }
}