package com.uamishop.backend.catalogo.controller;

import com.uamishop.backend.catalogo.service.ProductoService;
import com.uamishop.backend.catalogo.controller.dto.ProductoRequest;
import com.uamishop.backend.catalogo.controller.dto.ProductoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(
            @Valid @RequestBody ProductoRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.crear(request));
    }
    /**public ResponseEntity<ProductoResponse> crear(@RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.crear(request);
        return ResponseEntity.ok(response);
    }*/

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtenerPorId(@PathVariable UUID id) {
        ProductoResponse response = productoService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar() {
        return ResponseEntity.ok(productoService.listar());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ProductoRequest request
    ) {
        productoService.actualizar(id, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(@PathVariable UUID id) {
        productoService.activar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable UUID id) {
        productoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }



}
