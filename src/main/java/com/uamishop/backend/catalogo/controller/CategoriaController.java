package com.uamishop.backend.catalogo.controller;

import com.uamishop.backend.catalogo.service.CategoriaService;
import com.uamishop.backend.catalogo.controller.dto.CategoriaRequest;
import com.uamishop.backend.catalogo.controller.dto.CategoriaResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // =============================
    // CREAR
    // =============================
    @PostMapping
    public ResponseEntity<CategoriaResponse> crear(
            @RequestBody CategoriaRequest request
    ) {
        return ResponseEntity.ok(categoriaService.crear(request));
    }

    // =============================
    // OBTENER POR ID
    // =============================
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> obtenerPorId(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(categoriaService.obtenerPorId(id));
    }

    // =============================
    // LISTAR
    // =============================
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar() {
        return ResponseEntity.ok(categoriaService.listar());
    }

    // =============================
    // ACTUALIZAR
    // =============================
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> actualizar(
            @PathVariable UUID id,
            @RequestBody CategoriaRequest request
    ) {
        return ResponseEntity.ok(categoriaService.actualizar(id, request));
    }

    // =============================
    // ELIMINAR
    // =============================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable UUID id
    ) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}





