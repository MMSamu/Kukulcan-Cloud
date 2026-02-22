package com.uamishop.backend.ventas.controller;

import com.uamishop.backend.shared.domain.Money;
import com.uamishop.backend.ventas.controller.dto.AgregarProductoRequest;
import com.uamishop.backend.ventas.controller.dto.CarritoMapper;
import com.uamishop.backend.ventas.controller.dto.CarritoRequest;
import com.uamishop.backend.ventas.controller.dto.CarritoResponseDTO;
import com.uamishop.backend.ventas.domain.*;
import com.uamishop.backend.ventas.service.CarritoService;

import com.uamishop.backend.ventas.controller.dto.ModificarCantidadRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

/* Este controlador maneja las solicitudes relacionadas con los carritos de compra */
@Tag(name = "Carrito de Compras", description = "Endpoints para la gestión del carrito de compras")
@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    private final CarritoService service;

    // Inyección de dependencias a través del constructor
    public CarritoController(CarritoService service) {
        this.service = service;
    }

    // Endpoint para crear un nuevo carrito. Recibe el ID del cliente y devuelve el carrito creado.
    @Operation(summary = "Crear un nuevo carrito", description = "Crea un carrito vacío asociado a un cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Carrito creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la petición")
    })
    @PostMapping
    public ResponseEntity<CarritoResponseDTO> crear(@Valid @RequestBody CarritoRequest request) {
        Carrito carrito = service.crear(request.clienteId());
        return ResponseEntity.status(HttpStatus.CREATED).body(CarritoMapper.toDTO(carrito));
    }

    // Endpoint para obtener un carrito por su ID. Devuelve el carrito encontrado o un error si no existe.
    @Operation(summary = "Obtener carrito", description = "Recupera la información de un carrito por su ID.")
    @ApiResponse(responseCode = "200", description = "Carrito encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<CarritoResponseDTO> obtener(@PathVariable UUID id) {
        Carrito carrito = service.obtenerCarrito(new CarritoId(id));
        return ResponseEntity.ok(CarritoMapper.toDTO(carrito));
    }

    // Endpoint para agregar un producto al carrito. Recibe el ID del carrito, el ID del producto, 
    // la cantidad y el precio.
    @Operation(summary = "Agregar producto", description = "Agrega un producto nuevo o suma la cantidad si ya existe.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto agregado correctamente"),
            @ApiResponse(responseCode = "400", description = "Error de validación (ej. cantidad negativa)"),
            @ApiResponse(responseCode = "422", description = "Regla de negocio violada (ej. carrito lleno)")
    })
    @PostMapping("/{id}/productos")
    public ResponseEntity<CarritoResponseDTO> agregar(@PathVariable UUID id, @Valid @RequestBody AgregarProductoRequest request) {
        Money precioDominio = Money.pesos(request.precioMonto().doubleValue());
        Carrito carrito = service.agregarProducto(new CarritoId(id),request.productoId(), request.cantidad(), precioDominio);       
        return ResponseEntity.ok(CarritoMapper.toDTO(carrito));
    }

    // Endpoint para modificar la cantidad de un producto en el carrito. 
    // Recibe el ID del carrito, el ID del producto y la nueva cantidad.
    @Operation(summary = "Modificar cantidad", description = "Actualiza la cantidad de un producto. Si es 0, lo elimina.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cantidad modificada correctamente"),
            @ApiResponse(responseCode = "422", description = "Regla de negocio violada")
    })
    @PatchMapping("/{id}/productos/{pId}")
    public ResponseEntity<CarritoResponseDTO> modificar(@PathVariable UUID id, @PathVariable UUID pId, @Valid @RequestBody ModificarCantidadRequest request) { // <-- Cambio de DTO
        Carrito carrito = service.modificarCantidad(new CarritoId(id), pId, request.nuevaCantidad());
        return ResponseEntity.ok(CarritoMapper.toDTO(carrito));
    }

    // Endpoint para eliminar un producto del carrito. Recibe el ID del carrito y el ID del producto a eliminar.
    @Operation(summary = "Eliminar producto", description = "Quita un producto por completo del carrito.")
    @ApiResponse(responseCode = "200", description = "Producto eliminado")
    @DeleteMapping("/{id}/productos/{pId}")
    public ResponseEntity<CarritoResponseDTO> eliminar(@PathVariable UUID id, @PathVariable UUID pId) {
        Carrito carrito = service.eliminarProducto(new CarritoId(id), pId);
        return ResponseEntity.ok(CarritoMapper.toDTO(carrito));
    }

    // Endpoint para vaciar el carrito, eliminando todos los productos. Recibe el ID del carrito.
    @Operation(summary = "Vaciar carrito", description = "Elimina todos los productos del carrito.")
    @ApiResponse(responseCode = "204", description = "Carrito vaciado exitosamente")
    @DeleteMapping("/{id}/productos")
    public ResponseEntity<Void> vaciar(@PathVariable UUID id) {
        service.vaciar(new CarritoId(id));
        return ResponseEntity.noContent().build();
    }

    // Endpoint para iniciar el proceso de checkout del carrito. Recibe el ID del carrito 
    // y devuelve el carrito actualizado.
    @Operation(summary = "Iniciar Checkout", description = "Bloquea el carrito para iniciar el proceso de pago.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Checkout iniciado"),
            @ApiResponse(responseCode = "422", description = "Monto mínimo no alcanzado o carrito vacío")
    })
    @PostMapping("/{id}/checkout")
    public ResponseEntity<CarritoResponseDTO> checkout(@PathVariable UUID id) {
        Carrito carrito = service.iniciarCheckout(new CarritoId(id));
        return ResponseEntity.ok(CarritoMapper.toDTO(carrito));
    }

    // Endpoint para completar el proceso de checkout del carrito. 
    // Recibe el ID del carrito y devuelve el carrito actualizado.
    @Operation(summary = "Completar Checkout", description = "Finaliza el pago y marca el carrito como COMPLETADO.")
    @ApiResponse(responseCode = "200", description = "Checkout completado exitosamente")
    @PostMapping("/{id}/completar")
    public ResponseEntity<CarritoResponseDTO> completar(@PathVariable UUID id) {
        Carrito carrito = service.completarCheckout(new CarritoId(id));
        return ResponseEntity.ok(CarritoMapper.toDTO(carrito));
    }

    // Endpoint para abandonar el carrito, cancelando el proceso de compra. 
    // Recibe el ID del carrito y devuelve el carrito actualizado.
    @Operation(summary = "Abandonar Carrito", description = "Cancela el checkout y marca el carrito como ABANDONADO.")
    @ApiResponse(responseCode = "200", description = "Carrito abandonado")
    @PostMapping("/{id}/abandonar")
    public ResponseEntity<CarritoResponseDTO> abandonar(@PathVariable UUID id) {
        Carrito carrito = service.abandonar(new CarritoId(id));
        return ResponseEntity.ok(CarritoMapper.toDTO(carrito));
    }
}