package com.uamishop.orden.controller;

import com.uamishop.orden.service.OrdenService;
import com.uamishop.orden.controller.dto.*;
import com.uamishop.orden.domain.DireccionEnvio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "Crear una orden manual", description = "Crea una orden inicial consultando precios al Catálogo.")
    @PostMapping
    public ResponseEntity<OrdenResponseDTO> crear(@Valid @RequestBody OrdenRequest request) {
        DireccionEnvio direccion = mapDireccion(request.direccionEnvio());
        OrdenResumen resumen = ordenService.crear(request.clienteId(), direccion, request.items());
        return ResponseEntity.status(HttpStatus.CREATED).body(OrdenResponseDTO.fromResumen(resumen));
    }

    @Operation(summary = "Crear orden desde carrito")
@PostMapping("/{id}/orden")
    public ResponseEntity<OrdenResponseDTO> crearDesdeCarrito(@PathVariable UUID id) { 
        // 1. Buscamos la orden que el Listener ya debió insertar con ese ID
        // 2. Si no existe, el service lanzará la excepción "Orden no encontrada"
        OrdenResumen resumen = ordenService.obtenerOrden(id);
        
        // Opcional: Podrías llamar a un método de confirmación si lo necesitas
        // resumen = ordenService.confirmar(id);

        return ResponseEntity.status(HttpStatus.CREATED).body(OrdenResponseDTO.fromResumen(resumen));
    }

    private DireccionEnvio mapDireccion(DireccionEnvioRequest d) {
        if (d == null) return null;
        return DireccionEnvio.crear(
            d.calle() + " " + d.numeroExterior(),
            d.ciudad(), d.estado(), d.codigoPostal(), d.telefonoContacto()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenResponseDTO> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(ordenService.obtenerOrden(id)));
    }

    @GetMapping
    public ResponseEntity<List<OrdenResponseDTO>> listarTodas() {
        return ResponseEntity.ok(ordenService.listarOrdenes().stream().map(OrdenResponseDTO::fromResumen).toList());
    }

    @PostMapping("/{id}/confirmar")
    public ResponseEntity<OrdenResponseDTO> confirmar(@PathVariable UUID id) {
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(ordenService.confirmar(id)));
    }

    @PostMapping("/{id}/procesar-pago")
    public ResponseEntity<OrdenResponseDTO> procesarPago(@PathVariable UUID id, @Valid @RequestBody PagoRequest request) {
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(ordenService.procesarPago(id, request.referenciaPago())));
    }

    @PostMapping("/{id}/marcar-en-proceso")
    public ResponseEntity<OrdenResponseDTO> marcarEnProceso(@PathVariable UUID id) {
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(ordenService.marcarEnProceso(id)));
    }

    @PostMapping("/{id}/marcar-enviada")
    public ResponseEntity<OrdenResponseDTO> marcarEnviada(@PathVariable UUID id) {
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(ordenService.marcarEnviada(id)));
    }

    @PostMapping("/{id}/marcar-entregada")
    public ResponseEntity<OrdenResponseDTO> marcarEntregada(@PathVariable UUID id) {
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(ordenService.marcarEntregada(id)));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<OrdenResponseDTO> cancelar(@PathVariable UUID id, @Valid @RequestBody CancelacionRequest request) {
        return ResponseEntity.ok(OrdenResponseDTO.fromResumen(ordenService.cancelar(id, request.motivo())));
    }
}