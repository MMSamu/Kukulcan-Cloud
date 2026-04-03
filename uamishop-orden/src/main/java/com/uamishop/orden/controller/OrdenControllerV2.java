package com.uamishop.orden.controller;

import com.uamishop.orden.service.OrdenService;
import com.uamishop.orden.controller.dto.*;
import com.uamishop.orden.domain.DireccionEnvio;
import com.uamishop.shared.exception.ApiError;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal; // Importante para los montos
import java.util.List;
import java.util.UUID;

@Tag(name = "Ordenes", description = "Endpoints para la gestión de ordenes")
@RestController
@RequestMapping("/api/v2/ordenes")
public class OrdenControllerV2 {

    private final OrdenService ordenService;

    public OrdenControllerV2(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @Operation(summary = "Crear una orden", description = "Crea una orden inicial para un cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    @PostMapping
    public ResponseEntity<OrdenResponseDTO> crear(@Valid @RequestBody OrdenRequest request) {
        // Se pasa null en dirección y 0 en monto porque es una creación manual vacía
        OrdenResumen resumen = ordenService.crear(request.clienteId(), null, BigDecimal.ZERO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrdenResponseDTO.fromResumen(resumen));
    }

        @Operation(summary = "Crear orden desde carrito", description = "Crea una orden a partir de un carrito existente")
        @PostMapping("/{id}/orden")
        public ResponseEntity<OrdenResponseDTO> crearDesdeCarrito(
                @PathVariable UUID id, 
                @Valid @RequestBody DireccionEnvioRequest request) {

        // Extraemos la dirección del request para que no sea null
        DireccionEnvio direccion = DireccionEnvio.crear(
                request.calle() + " " + request.numeroExterior(),
                request.ciudad(),
                request.estado(),
                request.codigoPostal(),
                request.telefonoContacto());

        // Llamamos al service. Pasamos ZERO porque el monto real 
        // llegará por el CarritoFinalizadoListener (RabbitMQ)
        OrdenResumen resumen = ordenService.crearDesdeCarrito(
                request.clienteId(), 
                direccion, 
                BigDecimal.ZERO); 
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrdenResponseDTO.fromResumen(resumen));
        }

    @Operation(summary = "Obtener orden por ID")
    @GetMapping("/{id}")
    public ResponseEntity<OrdenResponseDTO> obtenerPorId(@PathVariable UUID id) {
        OrdenResumen resumen = ordenService.obtenerOrden(id);
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(resumen));
    }

    @Operation(summary = "Listar todas las órdenes")
    @GetMapping
    public ResponseEntity<List<OrdenResponseDTO>> listarTodas() {
        List<OrdenResponseDTO> response = ordenService.listarOrdenes().stream()
                .map(OrdenResponseDTO::fromResumen)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Confirmar una orden")
    @PostMapping("/{id}/confirmar")
    public ResponseEntity<OrdenResponseDTO> confirmar(@PathVariable UUID id) {
        OrdenResumen resumen = ordenService.confirmar(id);
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(resumen));
    }

    @Operation(summary = "Procesar pago")
    @PostMapping("/{id}/procesar-pago")
    public ResponseEntity<OrdenResponseDTO> procesarPago(
            @PathVariable UUID id,
            @Valid @RequestBody PagoRequest request) {
        OrdenResumen resumen = ordenService.procesarPago(id, request.referenciaPago());
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(resumen));
    }

    @Operation(summary = "Marcar en proceso")
    @PostMapping("/{id}/marcar-en-proceso")
    public ResponseEntity<OrdenResponseDTO> marcarEnProceso(@PathVariable UUID id) {
        OrdenResumen resumen = ordenService.marcarEnProceso(id);
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(resumen));
    }

    @Operation(summary = "Marcar como enviada")
    @PostMapping("/{id}/marcar-enviada")
    public ResponseEntity<OrdenResponseDTO> marcarEnviada(
            @PathVariable UUID id,
            @Valid @RequestBody EnvioRequest request) {
        OrdenResumen resumen = ordenService.marcarEnviada(id, request.numeroGuia());
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(resumen));
    }

    @Operation(summary = "Marcar como entregada")
    @PostMapping("/{id}/marcar-entregada")
    public ResponseEntity<OrdenResponseDTO> marcarEntregada(@PathVariable UUID id) {
        OrdenResumen resumen = ordenService.marcarEntregada(id);
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(resumen));
    }

    @Operation(summary = "Cancelar una orden")
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<OrdenResponseDTO> cancelar(
            @PathVariable UUID id,
            @Valid @RequestBody CancelacionRequest request) {
        OrdenResumen resumen = ordenService.cancelar(id, request.motivo());
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(resumen));
    }
}