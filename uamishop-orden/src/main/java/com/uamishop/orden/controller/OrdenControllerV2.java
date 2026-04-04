package com.uamishop.orden.controller;

import com.uamishop.orden.service.OrdenService;
import com.uamishop.orden.controller.dto.*;
import com.uamishop.orden.domain.DireccionEnvio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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

    // --- 1. CREAR UNA ORDEN (Con dirección inicial para evitar NullPointer) ---
    @Operation(summary = "Crear una orden", description = "Crea una orden inicial. Se recomienda enviar dirección para flujo completo.")
    @PostMapping
    public ResponseEntity<OrdenResponseDTO> crear(@Valid @RequestBody OrdenRequest request) {
        // Extraemos la dirección del request si existe, si no, el Service deberá manejarlo
        DireccionEnvio direccion = null;
        if (request.direccionEnvio() != null) {
            direccion = DireccionEnvio.crear(
                request.direccionEnvio().calle() + " " + request.direccionEnvio().numeroExterior(),
                request.direccionEnvio().ciudad(), 
                request.direccionEnvio().estado(), 
                request.direccionEnvio().codigoPostal(), 
                request.direccionEnvio().telefonoContacto());
        }

        // Usamos el método crear que acepta items (si los tienes en el DTO) o una lista vacía
        OrdenResumen resumen = ordenService.crear(request.clienteId(), direccion, BigDecimal.ZERO, request.items());
        return ResponseEntity.status(HttpStatus.CREATED).body(OrdenResponseDTO.fromResumen(resumen));
    }

    // --- 2. CREAR O ACTUALIZAR DESDE CARRITO ---
    @Operation(summary = "Crear orden desde carrito", description = "Vincula el ID de orden del carrito con los datos de envío")
    @PostMapping("/{id}/orden")
    public ResponseEntity<OrdenResponseDTO> crearDesdeCarrito(
            @PathVariable UUID id, 
            @Valid @RequestBody DireccionEnvioRequest request) {
        
        DireccionEnvio direccion = DireccionEnvio.crear(
                request.calle() + " " + request.numeroExterior(),
                request.ciudad(), 
                request.estado(), 
                request.codigoPostal(), 
                request.telefonoContacto());

        // Este método busca la orden y le inyecta la dirección y el monto (que llegará por Rabbit después)
        OrdenResumen resumen = ordenService.registrarOActualizarMonto(id, request.clienteId(), direccion, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrdenResponseDTO.fromResumen(resumen));
    }

    @Operation(summary = "Obtener orden por ID")
    @GetMapping("/{id}")
    public ResponseEntity<OrdenResponseDTO> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(ordenService.obtenerOrden(id)));
    }

    @Operation(summary = "Listar todas las órdenes")
    @GetMapping
    public ResponseEntity<List<OrdenResponseDTO>> listarTodas() {
        List<OrdenResponseDTO> response = ordenService.listarOrdenes().stream()
                .map(OrdenResponseDTO::fromResumen)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Confirmar una orden", description = "Cambia el estado de PENDIENTE a CONFIRMADA")
    @PostMapping("/{id}/confirmar")
    public ResponseEntity<OrdenResponseDTO> confirmar(@PathVariable UUID id) {
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(ordenService.confirmar(id)));
    }

    @Operation(summary = "Procesar pago", description = "Cambia el estado de CONFIRMADA a PREPARACION")
    @PostMapping("/{id}/procesar-pago")
    public ResponseEntity<OrdenResponseDTO> procesarPago(
            @PathVariable UUID id, 
            @Valid @RequestBody PagoRequest request) {
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(ordenService.procesarPago(id, request.referenciaPago())));
    }

    @Operation(summary = "Marcar en proceso", description = "Cambia el estado a PREPARACION (si no se ha pagado)")
    @PostMapping("/{id}/marcar-en-proceso")
    public ResponseEntity<OrdenResponseDTO> marcarEnProceso(@PathVariable UUID id) {
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(ordenService.marcarEnProceso(id)));
    }

    @Operation(summary = "Marcar como enviada", description = "Genera guía y cambia a ENVIADA. Requiere dirección previa.")
    @PostMapping("/{id}/marcar-enviada")
    public ResponseEntity<OrdenResponseDTO> marcarEnviada(@PathVariable UUID id) {
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(ordenService.marcarEnviada(id)));
    }

    @Operation(summary = "Marcar como entregada", description = "Cambia el estado de ENVIADA a ENTREGADA")
    @PostMapping("/{id}/marcar-entregada")
    public ResponseEntity<OrdenResponseDTO> marcarEntregada(@PathVariable UUID id) {
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(ordenService.marcarEntregada(id)));
    }

    @Operation(summary = "Cancelar una orden")
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<OrdenResponseDTO> cancelar(
            @PathVariable UUID id, 
            @Valid @RequestBody CancelacionRequest request) {
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(ordenService.cancelar(id, request.motivo())));
    }
}