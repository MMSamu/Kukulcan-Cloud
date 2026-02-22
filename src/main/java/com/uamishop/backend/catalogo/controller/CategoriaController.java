package com.uamishop.backend.catalogo.controller;

import com.uamishop.backend.catalogo.service.CategoriaService;
import com.uamishop.backend.catalogo.controller.dto.CategoriaRequest;
import com.uamishop.backend.catalogo.controller.dto.CategoriaResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uamishop.backend.shared.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@Tag(name = "Categorías", description = "Operaciones relacionadas con la gestión de categorías")
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
    @Operation(
            summary = "Crear categoría",
            description = "Permite crear una nueva categoría en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Categoría creada exitosamente",
                    content = @Content(schema = @Schema(implementation = CategoriaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error de validación",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @PostMapping
    public ResponseEntity<CategoriaResponse> crear(
            @Valid @RequestBody CategoriaRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoriaService.crear(request));
    }

    // =============================
    // OBTENER POR ID
    // =============================
    @Operation(summary = "Obtener categoría por ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoría encontrada",
                    content = @Content(schema = @Schema(implementation = CategoriaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoría no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> obtenerPorId(
            @Parameter(description = "UUID de la categoría", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(categoriaService.obtenerPorId(id));
    }

    // =============================
    // LISTAR
    // =============================
    @Operation(summary = "Listar todas las categorías")
    @ApiResponse(
            responseCode = "200",
            description = "Lista de categorías obtenida correctamente"
    )
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar() {
        return ResponseEntity.ok(categoriaService.listar());
    }

    // =============================
    // ACTUALIZAR
    // =============================
    @Operation(summary = "Actualizar categoría")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categoría actualizada correctamente",
                    content = @Content(schema = @Schema(implementation = CategoriaResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoría no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> actualizar(
            @Parameter(description = "UUID de la categoría a actualizar")
            @PathVariable UUID id,
            @Valid @RequestBody CategoriaRequest request
    ) {
        return ResponseEntity.ok(categoriaService.actualizar(id, request));
    }

    // =============================
    // ELIMINAR
    // =============================
    @Operation(summary = "Eliminar categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoría eliminada correctamente"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoría no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "UUID de la categoría a eliminar")
            @PathVariable UUID id
    ) {
        categoriaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}



    // =============================
    // CREAR
    // =============================
   /** @PostMapping
    public ResponseEntity<CategoriaResponse> crear(
           @Valid @RequestBody CategoriaRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.crear(request));
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
    public ResponseEntity<List<CategoriaResponse>> listar(

    ) {
        return ResponseEntity.ok(categoriaService.listar());
    }

    // =============================
    // ACTUALIZAR
    // =============================
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody CategoriaRequest request
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
}*/





