package com.uamishop.backend.orden.controller;

import com.uamishop.backend.orden.api.OrdenesApi;
import com.uamishop.backend.orden.api.OrdenResumen;
import com.uamishop.backend.orden.domain.DireccionEnvio;
import com.uamishop.backend.shared.exception.ApiError;


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

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para la gestión de órdenes.
 *
 * Depende exclusivamente de OrdenesApi (la interfaz pública del módulo).
 * No importa ni referencia OrdenService directamente.
 */
@Tag(name = "Ordenes", description = "Endpoints para la gestión de ordenes")
@RestController
@RequestMapping("/api/v2/ordenes")
public class OrdenControllerV2 {

        // Solo conoce la API pública del módulo
        private final OrdenesApi ordenesApi;

        public OrdenControllerV2(OrdenesApi ordenesApi) {
                this.ordenesApi = ordenesApi;
        }

        // ── POST /api/v2/ordenes ──────────────────────────────────────────────────

        @Operation(summary = "Crear una orden", description = "Crea una orden vacía para un cliente")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Orden creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
        })
        @PostMapping
        public ResponseEntity<OrdenResponseDTO> crear(@Valid @RequestBody OrdenRequest request) {
                OrdenResumen resumen = ordenesApi.crear(request.clienteId(), null);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(OrdenResponseDTO.fromResumen(resumen));
        }

        // ── POST /api/v2/ordenes/{id}/orden ──────────────────────────────────────

        @Operation(summary = "Crear orden desde carrito", description = "Crea una orden a partir de un carrito existente")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Orden creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Carrito no encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
        })
        @PostMapping("/{id}/orden")
        public ResponseEntity<OrdenResponseDTO> crearDesdeCarrito(
                        @PathVariable UUID id,
                        @Valid @RequestBody DireccionEnvioRequest request) {

                DireccionEnvio direccion = DireccionEnvio.crear(
                                request.calle(),
                                request.ciudad(),
                                request.estado(),
                                request.codigoPostal(),
                                request.telefonoContacto());

                OrdenResumen resumen = ordenesApi.crearDesdeCarrito(id, direccion);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(OrdenResponseDTO.fromResumen(resumen));
        }

        // ── GET /api/v2/ordenes/{id} ──────────────────────────────────────────────

        @Operation(summary = "Obtener orden por ID", description = "Busca una orden por su UUID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Orden encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Orden no encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
        })
        @GetMapping("/{id}")
        public ResponseEntity<OrdenResponseDTO> obtenerPorId(@PathVariable UUID id) {
                OrdenResumen resumen = ordenesApi.obtenerOrden(id);
                return ResponseEntity.ok(OrdenResponseDTO.fromResumen(resumen));
        }

        // ── GET /api/v2/ordenes ───────────────────────────────────────────────────

        @Operation(summary = "Listar todas las órdenes", description = "Retorna el resumen de todas las órdenes")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Órdenes encontradas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
        })
        @GetMapping
        public ResponseEntity<List<OrdenResponseDTO>> listarTodas() {
                List<OrdenResponseDTO> response = ordenesApi.listarOrdenes().stream()
                                .map(OrdenResponseDTO::fromResumen)
                                .toList();
                return ResponseEntity.ok(response);
        }

        // ── POST /api/v2/ordenes/{id}/confirmar ───────────────────────────────────

        @Operation(summary = "Confirmar una orden", description = "Confirma una orden en estado PENDIENTE")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Orden confirmada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Estado inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
        })
        @PostMapping("/{id}/confirmar")
        public ResponseEntity<OrdenResponseDTO> confirmar(@PathVariable UUID id) {
                OrdenResumen resumen = ordenesApi.confirmar(id);
                return ResponseEntity.ok(OrdenResponseDTO.fromResumen(resumen));
        }

        // ── POST /api/v2/ordenes/{id}/procesar-pago ───────────────────────────────

        @Operation(summary = "Procesar pago", description = "Procesa el pago de una orden CONFIRMADA")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Pago procesado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Estado inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
        })
        @PostMapping("/{id}/procesar-pago")
        public ResponseEntity<OrdenResponseDTO> procesarPago(
                        @PathVariable UUID id,
                        @RequestBody PagoRequest request) {
                OrdenResumen resumen = ordenesApi.procesarPago(id, request.referenciaPago());
                return ResponseEntity.ok(OrdenResponseDTO.fromResumen(resumen));
        }

        // ── POST /api/v2/ordenes/{id}/marcar-en-proceso ───────────────────────────

        @Operation(summary = "Marcar en proceso", description = "Marca una orden como en preparación")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Orden en proceso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Estado inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
        })
        @PostMapping("/{id}/marcar-en-proceso")
        public ResponseEntity<OrdenResponseDTO> marcarEnProceso(@PathVariable UUID id) {
                OrdenResumen resumen = ordenesApi.marcarEnProceso(id);
                return ResponseEntity.ok(OrdenResponseDTO.fromResumen(resumen));
        }

        // ── POST /api/v2/ordenes/{id}/marcar-enviada ──────────────────────────────

        @Operation(summary = "Marcar como enviada", description = "Marca una orden como enviada con número de guía")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Orden enviada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Estado inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
        })
        @PostMapping("/{id}/marcar-enviada")
        public ResponseEntity<OrdenResponseDTO> marcarEnviada(
                        @PathVariable UUID id,
                        @RequestBody EnvioRequest request) {
                OrdenResumen resumen = ordenesApi.marcarEnviada(id, request.numeroGuia());
                return ResponseEntity.ok(OrdenResponseDTO.fromResumen(resumen));
        }

        // ── POST /api/v2/ordenes/{id}/marcar-entregada ────────────────────────────

        @Operation(summary = "Marcar como entregada", description = "Marca una orden enviada como entregada")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Orden entregada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Estado inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
        })
        @PostMapping("/{id}/marcar-entregada")
        public ResponseEntity<OrdenResponseDTO> marcarEntregada(@PathVariable UUID id) {
                OrdenResumen resumen = ordenesApi.marcarEntregada(id);
                return ResponseEntity.ok(OrdenResponseDTO.fromResumen(resumen));
        }

        // ── POST /api/v2/ordenes/{id}/cancelar ───────────────────────────────────

        @Operation(summary = "Cancelar una orden", description = "Cancela una orden indicando el motivo")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Orden cancelada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrdenResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Estado inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class)))
        })
        @PostMapping("/{id}/cancelar")
        public ResponseEntity<OrdenResponseDTO> cancelar(
                        @PathVariable UUID id,
                        @RequestBody CancelacionRequest request) {
                OrdenResumen resumen = ordenesApi.cancelar(id, request.motivo());
                return ResponseEntity.ok(OrdenResponseDTO.fromResumen(resumen));
        }
}
