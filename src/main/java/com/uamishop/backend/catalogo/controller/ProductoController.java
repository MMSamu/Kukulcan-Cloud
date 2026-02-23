/**
 * @file ProductoController.java
 * @brief Controlador REST encargado de gestionar las operaciones CRUD de productos.
 *
 * Expone endpoints para crear, consultar, actualizar, activar y desactivar productos.
 * Utiliza ProductoService para delegar la lógica de negocio.
 */
package com.uamishop.backend.catalogo.controller;

// Importa el servicio que contiene la lógica de negocio de productos
import com.uamishop.backend.catalogo.service.ProductoService;

// Importa el DTO que recibe datos desde el cliente
import com.uamishop.backend.catalogo.controller.dto.ProductoRequest;

// Importa el DTO que se envía como respuesta al cliente
import com.uamishop.backend.catalogo.controller.dto.ProductoResponse;

// Permite activar validaciones automáticas en los DTOs
import jakarta.validation.Valid;

// Permite utilizar códigos de estado HTTP (200, 201, 204, etc.)
import org.springframework.http.HttpStatus;

// Permite construir respuestas HTTP personalizadas
import org.springframework.http.ResponseEntity;

// Importa anotaciones REST como @RestController, @GetMapping, etc.
import org.springframework.web.bind.annotation.*;

// Importaciones de Swagger/OpenAPI para documentación automática
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

// Importa colección List
import java.util.List;

// Importa UUID para identificadores únicos
import java.util.UUID;

/**
 * @RestController Indica que esta clase es un controlador REST.
 * Devuelve respuestas en formato JSON automáticamente.
 */
@RestController

/**
 * Define la ruta base para todos los endpoints de este controlador.
 * Todas las rutas comenzarán con /api/productos
 */
@RequestMapping("/api/productos")
public class ProductoController {

    /**
     * Servicio que contiene la lógica de negocio de productos.
     * Se declara final porque no debe modificarse después de la inyección.
     */
    private final ProductoService productoService;

    /**
     * Constructor que permite la inyección de dependencias.
     * Spring inyecta automáticamente ProductoService.
     *
     * @param productoService Servicio encargado de la lógica de negocio
     */
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * Endpoint para crear un nuevo producto.
     *
     * @Operation Documenta la operación en Swagger.
     */
    @Operation(summary = "Crear un nuevo producto")

    /**
     * Define la respuesta HTTP esperada.
     * 201 indica que el recurso fue creado correctamente.
     */
    @ApiResponse(responseCode = "201", description = "Producto creado correctamente",
            content = @Content(schema = @Schema(implementation = ProductoResponse.class)))

    /**
     * Indica que este método responde a solicitudes HTTP POST.
     */
    @PostMapping
    public ResponseEntity<ProductoResponse> crear(

            /**
             * @Valid activa las validaciones definidas en ProductoRequest.
             * @RequestBody convierte el JSON recibido en un objeto Java.
             */
            @Valid @RequestBody ProductoRequest request
    ) {

        // Devuelve estado 201 (CREATED) junto con el producto creado
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productoService.crear(request));
    }

    /**
     * Endpoint para obtener un producto por su ID.
     */
    @Operation(summary = "Obtener producto por ID")

    /**
     * Define posibles respuestas HTTP.
     */
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })

    /**
     * Responde a solicitudes GET con un ID en la URL.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtenerPorId(

            /**
             * @Parameter documenta el parámetro en Swagger.
             * @PathVariable extrae el valor desde la URL.
             */
            @Parameter(description = "ID del producto")
            @PathVariable UUID id) {

        // Devuelve estado 200 (OK) con el producto encontrado
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    /**
     * Endpoint para listar todos los productos.
     */
    @Operation(summary = "Listar todos los productos")

    /**
     * Respuesta HTTP esperada para listado.
     */
    @ApiResponse(responseCode = "200", description = "Lista de productos",
            content = @Content(schema = @Schema(implementation = ProductoResponse.class)))

    /**
     * Responde a solicitudes GET sin parámetros.
     */
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar() {

        // Devuelve estado 200 con la lista de productos
        return ResponseEntity.ok(productoService.listar());
    }

    /**
     * Endpoint para actualizar un producto existente.
     */
    @Operation(summary = "Actualizar un producto")

    /**
     * Define posibles respuestas HTTP.
     */
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = ProductoResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })

    /**
     * Responde a solicitudes PUT con ID en la URL.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(

            /**
             * Extrae el ID desde la URL.
             */
            @Parameter(description = "ID del producto")
            @PathVariable UUID id,

            /**
             * Recibe los nuevos datos en formato JSON.
             */
            @Valid @RequestBody ProductoRequest request
    ) {

        // Devuelve estado 200 con el producto actualizado
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    /**
     * Endpoint para activar un producto.
     * Se utiliza PATCH porque solo modifica parcialmente el recurso.
     */
    @Operation(summary = "Activar un producto")

    @ApiResponse(responseCode = "204", description = "Producto activado correctamente")

    /**
     * Ruta: PATCH /api/productos/{id}/activar
     */
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(@PathVariable UUID id) {

        // Llama al servicio para activar el producto
        productoService.activar(id);

        // Devuelve 204 (No Content) indicando éxito sin cuerpo
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint para desactivar un producto.
     */
    @Operation(summary = "Desactivar un producto")

    @ApiResponse(responseCode = "204", description = "Producto desactivado correctamente")

    /**
     * Ruta: PATCH /api/productos/{id}/desactivar
     */
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivar(@PathVariable UUID id) {

        // Llama al servicio para desactivar el producto
        productoService.desactivar(id);

        // Devuelve 204 (No Content)
        return ResponseEntity.noContent().build();
    }
}