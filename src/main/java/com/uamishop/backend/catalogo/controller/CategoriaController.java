/**
 * @file CategoriaController.java
 * @brief Controlador REST que gestiona las operaciones CRUD de categorías.
 *
 * Esta clase expone endpoints HTTP para crear, consultar, actualizar
 * y eliminar categorías dentro del sistema.
 *
 * Utiliza Swagger/OpenAPI para documentar automáticamente la API.
 */
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

/**
 * @Tag Define el grupo en Swagger donde aparecerán estos endpoints.
 */
@Tag(name = "Categorías", description = "Operaciones relacionadas con la gestión de categorías")

/**
 * Indica que esta clase es un controlador REST.
 * Devuelve respuestas en formato JSON automáticamente.
 */
@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {


    private final CategoriaService categoriaService;

    /**
     * Constructor que inyecta el servicio.
     * Spring lo utiliza para inyección de dependencias.
     *
     * @param categoriaService Servicio de categorías
     */
    public CategoriaController(CategoriaService categoriaService) {

        // Asigna el servicio recibido al atributo de la clase
        this.categoriaService = categoriaService;
    }

    // =============================
    // CREAR
    // =============================

    /**
     * Documentación Swagger para la operación de crear categoría.
     */
    @Operation(
            summary = "Crear categoría",
            description = "Permite crear una nueva categoría en el sistema"
    )

    /**
     * Define las posibles respuestas HTTP del endpoint.
     */
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

    /**
     * Define que este método responde a solicitudes HTTP POST.
     */
    @PostMapping
    public ResponseEntity<CategoriaResponse> crear(

            /**
             * @Valid activa validaciones del DTO.
             * @RequestBody convierte el JSON recibido en un objeto Java.
             */
            @Valid @RequestBody CategoriaRequest request
    ) {

        // Devuelve estado 201 (CREATED) y el objeto creado
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoriaService.crear(request));
    }

    // =============================
    // OBTENER POR ID
    // =============================

    /**
     * Documentación Swagger para obtener categoría por ID.
     */
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

    /**
     * Define que responde a solicitudes GET con un ID en la URL.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> obtenerPorId(

            /**
             * @Parameter agrega documentación en Swagger.
             * @PathVariable extrae el valor del ID desde la URL.
             */
            @Parameter(description = "UUID de la categoría", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id
    ) {

        // Devuelve estado 200 (OK) con la categoría encontrada
        return ResponseEntity.ok(categoriaService.obtenerPorId(id));
    }

    // =============================
    // LISTAR
    // =============================

    /**
     * Documentación Swagger para listar categorías.
     */
    @Operation(summary = "Listar todas las categorías")

    @ApiResponse(
            responseCode = "200",
            description = "Lista de categorías obtenida correctamente"
    )

    /**
     * Responde a solicitudes GET sin parámetros.
     */
    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listar() {

        // Devuelve estado 200 con la lista de categorías
        return ResponseEntity.ok(categoriaService.listar());
    }

    // =============================
    // ACTUALIZAR
    // =============================

    /**
     * Documentación Swagger para actualizar categoría.
     */
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

    /**
     * Responde a solicitudes PUT con ID en la URL.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponse> actualizar(

            /**
             * Extrae el ID desde la URL.
             */
            @Parameter(description = "UUID de la categoría a actualizar")
            @PathVariable UUID id,

            /**
             * Recibe los nuevos datos en formato JSON.
             */
            @Valid @RequestBody CategoriaRequest request
    ) {

        // Devuelve estado 200 con la categoría actualizada
        return ResponseEntity.ok(categoriaService.actualizar(id, request));
    }

    // =============================
    // ELIMINAR
    // =============================

    /**
     * Documentación Swagger para eliminar categoría.
     */
    @Operation(summary = "Eliminar categoría")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoría eliminada correctamente"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categoría no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class))
            )
    })

    /**
     * Responde a solicitudes DELETE con ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(

            /**
             * Extrae el ID desde la URL.
             */
            @Parameter(description = "UUID de la categoría a eliminar")
            @PathVariable UUID id
    ) {

        // Llama al servicio para eliminar la categoría
        categoriaService.eliminar(id);

        // Devuelve estado 204 (No Content) indicando éxito sin cuerpo
        return ResponseEntity.noContent().build();
    }
}



