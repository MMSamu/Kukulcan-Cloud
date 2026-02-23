package com.uamishop.backend.orden.controller;

import com.uamishop.backend.orden.domain.*;
import com.uamishop.backend.orden.service.OrdenService;
import com.uamishop.backend.orden.controller.dto.*;
import com.uamishop.backend.shared.exception.ApiError;
import com.uamishop.backend.ventas.domain.CarritoId;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.List;

@Tag(name = "Ordenes", description = "Endpoints para la gestión de ordenes")
@RestController
@RequestMapping("/api/ordenes")

public class OrdenController {

    private final OrdenService ordenService;

    // Constructor
    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @Operation(summary = "Crear una orden", description = "Crea una orden para un cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Cliente no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),

            @ApiResponse(responseCode = "500", description = "Error al crear la orden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })

    @PostMapping
    public ResponseEntity<OrdenResponseDTO> crear(@Valid @RequestBody OrdenRequest request) {
        // Crear una orden
        Orden orden = ordenService.crear(request.clienteId(), null);
        // Devolver la orden creada
        return ResponseEntity.status(HttpStatus.CREATED).body(OrdenResponseDTO.fromDomain(orden));
    }

    @Operation(summary = "Crear una orden desde el carrito", description = "Crea una orden desde el carrito de un cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Cliente no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),

            @ApiResponse(responseCode = "500", description = "Error al crear la orden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })

    // Crear una orden desde el carrito
    @PostMapping("/{id}/orden")
    public ResponseEntity<OrdenResponseDTO> crearDesdeCarrito(@PathVariable UUID id,
            @Valid @RequestBody DireccionEnvioRequest request) {
        // Crear una dirección de envío
        DireccionEnvio direccion = DireccionEnvio.crear(
                request.calle(),
                request.ciudad(),
                request.estado(),
                request.codigoPostal(),
                request.telefonoContacto());
        Orden orden = ordenService.crearDesdeCarrito(new CarritoId(id), direccion);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrdenResponseDTO.fromDomain(orden));
    }

    // Buscar una orden por su ID
    @Operation(summary = "Buscar por ID", description = "Busca una orden por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden encontrada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Orden no encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),

            @ApiResponse(responseCode = "500", description = "Error al buscar la orden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })

    // Buscar una orden por su ID
    @GetMapping("/{id}")
    public ResponseEntity<OrdenResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(OrdenResponseDTO.fromDomain(ordenService.buscarPorId(id)));
    }

    // Buscar todas las ordenes
    @Operation(summary = "Buscar todas las ordenes", description = "Busca todas las ordenes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ordenes encontradas exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Ordenes no encontradas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),

            @ApiResponse(responseCode = "500", description = "Error al buscar las ordenes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })

    // Buscar todas las ordenes
    @GetMapping
    public ResponseEntity<List<OrdenResponseDTO>> buscarTodas() {
        List<OrdenResponseDTO> response = ordenService.buscarTodas()
                .stream()
                .map(OrdenResponseDTO::fromDomain)
                .toList();
        return ResponseEntity.ok(response);
    }

    // Confirmar una orden
    @Operation(summary = "Confirmar una orden", description = "Confirma una orden")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden confirmada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Orden no encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),

            @ApiResponse(responseCode = "500", description = "Error al confirmar la orden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })

    // Confirmar una orden
    @PostMapping("/{clienteId}/confirmar")
    public ResponseEntity<OrdenResponseDTO> confirmar(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(OrdenResponseDTO.fromDomain(ordenService.confirmar(clienteId)));
    }

    // Procesar pago
    @Operation(summary = "Procesar pago", description = "Procesa el pago de una orden")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pago procesado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Orden no encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),

            @ApiResponse(responseCode = "500", description = "Error al procesar el pago", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })

    // Procesar pago
    @PostMapping("/{clienteId}/procesar-pago")
    public ResponseEntity<OrdenResponseDTO> procesarPago(@PathVariable UUID clienteId,
            @RequestBody PagoRequest request) {
        return ResponseEntity
                .ok(OrdenResponseDTO.fromDomain(ordenService.procesarPago(clienteId, request.referenciaPago())));
    }

    // Marcar como en proceso
    @Operation(summary = "Marcar como en proceso", description = "Marca una orden como en proceso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden marcada como en proceso exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Orden no encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),

            @ApiResponse(responseCode = "500", description = "Error al marcar la orden como en proceso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })

    // Marcar como en proceso
    @PostMapping("/{clienteId}/marcar-en-proceso")
    public ResponseEntity<OrdenResponseDTO> marcarEnProceso(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(OrdenResponseDTO.fromDomain(ordenService.marcarEnProceso(clienteId)));
    }

    // Marcar como enviada
    @Operation(summary = "Marcar como enviada", description = "Marca una orden como enviada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden marcada como enviada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Orden no encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),

            @ApiResponse(responseCode = "500", description = "Error al marcar la orden como enviada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })

    // Marcar como enviada
    @PostMapping("/{id}/marcar-enviada")
    public ResponseEntity<OrdenResponseDTO> marcarEnviada(@PathVariable UUID id, @RequestBody EnvioRequest request) {
        return ResponseEntity.ok(OrdenResponseDTO.fromDomain(ordenService.marcarEnviada(id, request.numeroGuia())));
    }

    // Marcar como entregada
    @Operation(summary = "Marcar como entregada", description = "Marca una orden como entregada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden marcada como entregada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Orden no encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),

            @ApiResponse(responseCode = "500", description = "Error al marcar la orden como entregada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })

    // Marcar como entregada
    @PostMapping("/{id}/marcar-entregada")
    public ResponseEntity<OrdenResponseDTO> marcarEntregada(@PathVariable UUID id) {
        return ResponseEntity.ok(OrdenResponseDTO.fromDomain(ordenService.marcarEntregada(id)));
    }

    // Cancelar una orden
    @Operation(summary = "Cancelar una orden", description = "Cancela una orden")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden cancelada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Orden no encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),

            @ApiResponse(responseCode = "500", description = "Error al cancelar la orden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })

    // Cancelar una orden
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<OrdenResponseDTO> cancelar(@PathVariable UUID id, @RequestBody CancelacionRequest request) {
        return ResponseEntity.ok(OrdenResponseDTO.fromDomain(ordenService.cancelar(id, request.motivo())));
    }

}
