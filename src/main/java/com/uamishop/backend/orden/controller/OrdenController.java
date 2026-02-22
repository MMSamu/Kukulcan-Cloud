package com.uamishop.backend.orden.controller;

import com.uamishop.backend.orden.domain.*;
import com.uamishop.backend.orden.service.OrdenService;
import com.uamishop.backend.orden.controller.dto.CancelacionRequest;
import com.uamishop.backend.orden.controller.dto.EnvioRequest;
import com.uamishop.backend.orden.controller.dto.OrdenRequest;
import com.uamishop.backend.orden.controller.dto.PagoRequest;
import com.uamishop.backend.orden.controller.dto.CancelacionResponseDTO;
import com.uamishop.backend.orden.controller.dto.EnvioResponseDTO;
import com.uamishop.backend.orden.controller.dto.PagoResponseDTO;
import com.uamishop.backend.orden.controller.dto.OrdenResponseDTO;
import com.uamishop.backend.shared.exception.ApiError;

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
        Orden orden = ordenService.crear(request.clienteId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenService.crear(orden));
    }

    @Operation(summary = "Crear una orden desde el carrito", description = "Crea una orden desde el carrito de un cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Cliente no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),

            @ApiResponse(responseCode = "500", description = "Error al crear la orden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })

    @PostMapping("/{id}/orden")
    public Orden crearDesdeCarrito(@PathVariable UUID id, String direccionEnvio) {
        return ordenService.crearDesdeCarrito(new OrdenId(id), direccionEnvio);
    }

    @Operation(summary = "Buscar por ID", description = "Busca una orden por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden encontrada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Orden no encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),

            @ApiResponse(responseCode = "500", description = "Error al buscar la orden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })

    @GetMapping("/{id}")
    public Orden buscarPorId(@PathVariable UUID clienteId) {
        return ordenService.buscarPorId(clienteId);
    }

    @Operation(summary = "Buscar todas las ordenes", description = "Busca todas las ordenes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ordenes encontradas exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Ordenes no encontradas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),

            @ApiResponse(responseCode = "500", description = "Error al buscar las ordenes", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })

    @GetMapping
    public List<Orden> buscarTodas() {
        return ordenService.buscarTodas();
    }

    @Operation(summary = "Confirmar una orden", description = "Confirma una orden")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden confirmada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Orden no encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),

            @ApiResponse(responseCode = "500", description = "Error al confirmar la orden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })

    @PostMapping("/{clienteId}/confirmar")
    public Orden confirmar(@PathVariable UUID clienteId) {
        return ordenService.confirmar(clienteId);
    }

    @Operation(summary = "Procesar pago", description = "Procesa el pago de una orden")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pago procesado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Orden no encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),

            @ApiResponse(responseCode = "500", description = "Error al procesar el pago", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })

    @PostMapping("/{clienteId}/procesar-pago")
    public Orden procesarPago(@PathVariable UUID clienteId, String referenciaPago) {
        return ordenService.procesarPago(clienteId, referenciaPago);
    }

    @Operation(summary = "Marcar como en proceso", description = "Marca una orden como en proceso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden marcada como en proceso exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Orden no encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),

            @ApiResponse(responseCode = "500", description = "Error al marcar la orden como en proceso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })

    @PostMapping("/{clienteId}/marcar-en-proceso")
    public Orden marcarEnProceso(@PathVariable UUID clienteId) {
        return ordenService.marcarEnProceso(clienteId);
    }

    @Operation(summary = "Marcar como enviada", description = "Marca una orden como enviada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden marcada como enviada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Orden no encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),

            @ApiResponse(responseCode = "500", description = "Error al marcar la orden como enviada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })

    @PostMapping("/{clienteId}/marcar-enviada")
    public Orden marcarEnviada(@PathVariable UUID ordenID, String numeroGuia) {
        return ordenService.marcarEnviada(ordenID, numeroGuia);
    }

    @Operation(summary = "Marcar como entregada", description = "Marca una orden como entregada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden marcada como entregada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Orden no encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),

            @ApiResponse(responseCode = "500", description = "Error al marcar la orden como entregada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })

    @PostMapping("/{clienteId}/marcar-entregada")
    public Orden marcarEntregada(@PathVariable UUID clienteId) {
        return ordenService.marcarEntregada(clienteId);
    }

    @Operation(summary = "Cancelar una orden", description = "Cancela una orden")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden cancelada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),

            @ApiResponse(responseCode = "400", description = "Orden no encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),

            @ApiResponse(responseCode = "500", description = "Error al cancelar la orden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
    })

    @PostMapping("/{clienteId}/cancelar")
    public Orden cancelar(@PathVariable UUID clienteId, String motivo) {
        return ordenService.cancelar(clienteId, motivo);
    }

}
