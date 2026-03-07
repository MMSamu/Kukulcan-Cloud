/**
 * @file ProductoController.java
 * @brief Controlador REST encargado de gestionar las operaciones CRUD de productos.
 *
 * Expone endpoints para crear, consultar, actualizar, activar y desactivar productos.
 * Utiliza ProductoService para delegar la lógica de negocio.
 */
package com.uamishop.backend.catalogo.controller;

import com.uamishop.backend.catalogo.service.ProductoService;
import com.uamishop.backend.catalogo.controller.dto.ProductoRequest;
import com.uamishop.backend.catalogo.controller.dto.ProductoResponse;

import com.uamishop.backend.catalogo.service.ProductoEstadisticasService;
import com.uamishop.backend.catalogo.controller.dto.ProductoEstadisticasResponse;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

        private final ProductoService productoService;
        private final ProductoEstadisticasService estadisticasService;

        public ProductoController(ProductoService productoService, ProductoEstadisticasService estadisticasService) {
                this.productoService = productoService;
                this.estadisticasService = estadisticasService;
        }

        // =====================================================
        // CREAR PRODUCTO
        // =====================================================

        @Operation(summary = "Crear un nuevo producto")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Producto creado correctamente", content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
                        @ApiResponse(responseCode = "422", description = "Regla de negocio violada"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @PostMapping
        public ResponseEntity<ProductoResponse> crear(
                        @Valid @RequestBody ProductoRequest request) {
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(productoService.crear(request));
        }

        // =====================================================
        // OBTENER POR ID
        // =====================================================

        @Operation(summary = "Obtener producto por ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Producto encontrado", content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
                        @ApiResponse(responseCode = "422", description = "Regla de negocio violada"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @GetMapping("/{id}")
        public ResponseEntity<ProductoResponse> obtenerPorId(
                        @Parameter(description = "ID del producto") @PathVariable UUID id) {

                return ResponseEntity.ok(productoService.obtenerPorId(id));
        }

        // =====================================================
        // LISTAR PRODUCTOS
        // =====================================================

        @Operation(summary = "Listar todos los productos")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Lista de productos", content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @GetMapping
        public ResponseEntity<List<ProductoResponse>> listar() {
                return ResponseEntity.ok(productoService.listar());
        }

        // =====================================================
        // ACTUALIZAR PRODUCTO
        // =====================================================

        @Operation(summary = "Actualizar un producto")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente", content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
                        @ApiResponse(responseCode = "422", description = "Regla de negocio violada"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @PutMapping("/{id}")
        public ResponseEntity<ProductoResponse> actualizar(
                        @Parameter(description = "ID del producto") @PathVariable UUID id,
                        @Valid @RequestBody ProductoRequest request) {
                return ResponseEntity.ok(productoService.actualizar(id, request));
        }

        // =====================================================
        // ACTIVAR PRODUCTO
        // =====================================================

        @Operation(summary = "Activar un producto")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Producto activado correctamente"),
                        @ApiResponse(responseCode = "422", description = "Regla de negocio violada"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @PatchMapping("/{id}/activar")
        public ResponseEntity<Void> activar(@PathVariable UUID id) {
                productoService.activar(id);
                return ResponseEntity.noContent().build();
        }

        // =====================================================
        // DESACTIVAR PRODUCTO
        // =====================================================

        @Operation(summary = "Desactivar un producto")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Producto desactivado correctamente"),
                        @ApiResponse(responseCode = "422", description = "Regla de negocio violada"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @PatchMapping("/{id}/desactivar")
        public ResponseEntity<Void> desactivar(@PathVariable UUID id) {
                productoService.desactivar(id);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "Obtener productos más vendidos")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Lista de productos más vendidos", content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })

        @GetMapping("/mas-vendidos")
        public ResponseEntity<List<ProductoEstadisticasResponse>> obtenerMasVendidos(
                        @RequestParam(defaultValue = "10") int limit) {
                List<ProductoEstadisticasResponse> resultado = estadisticasService.obtenerMasVendidos(limit)
                                .stream()
                                .map(e -> new ProductoEstadisticasResponse(
                                                e.getVentasTotales(),
                                                e.getCantidadVendida(),
                                                e.getVecesAgregadoAlCarrito(),
                                                e.getUltimaVentaAt() != null
                                                                ? e.getUltimaVentaAt().atZone(ZoneId.systemDefault())
                                                                                .toLocalDateTime()
                                                                : null))
                                .collect(Collectors.toList());
                return ResponseEntity.ok(resultado);
        }

        @Operation(summary = "Obtener estadísticas de un producto")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Estadísticas del producto", content = @Content(schema = @Schema(implementation = ProductoEstadisticasResponse.class))),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @GetMapping("/{id}/estadisticas")
        public ResponseEntity<ProductoEstadisticasResponse> obtenerEstadisticasPorId(@PathVariable UUID id) {
                var e = estadisticasService.obtenerEstadisticas(id);

                if (e == null) {
                        return ResponseEntity.notFound().build();
                }

                ProductoEstadisticasResponse response = new ProductoEstadisticasResponse(
                                e.getVentasTotales(),
                                e.getCantidadVendida(),
                                e.getVecesAgregadoAlCarrito(),
                                e.getUltimaVentaAt() != null
                                                ? e.getUltimaVentaAt().atZone(ZoneId.systemDefault()).toLocalDateTime()
                                                : null);

                return ResponseEntity.ok(response);
        }
}