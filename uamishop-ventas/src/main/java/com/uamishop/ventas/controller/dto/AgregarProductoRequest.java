package com.uamishop.ventas.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

/**
 * Este Record actúa como un "Contenedor de Datos de Entrada".
 * Se usa cuando el Frontend envía información para agregar un producto.
 */
public record AgregarProductoRequest(

    // Validamos que el cliente envíe forzosamente el ID del producto
    @Schema(description = "Identificador único del producto", example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "El ID del producto es obligatorio")
    UUID productoId,

    // Validamos que la cantidad sea un número entero positivo y no excesivo
    @Schema(description = "Cantidad a agregar al carrito", example = "2")
    @Positive(message = "La cantidad debe ser mayor a cero")
    @Max(value = 10, message = "Máximo 10 unidades permitidas por producto")
    int cantidad
    
) {}