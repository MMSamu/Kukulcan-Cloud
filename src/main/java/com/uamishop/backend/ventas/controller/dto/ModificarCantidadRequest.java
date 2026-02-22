package com.uamishop.backend.ventas.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * * @brief DTO para modificar la cantidad de un producto que ya está en el carrito.
 * * @note Lau, creé este DTO específico para el endpoint PUT.
 * Usé @PositiveOrZero porque en la lógica de Carrito.java programaste
 * que si la cantidad es 0, el producto se elimina automáticamente.
 */
public record ModificarCantidadRequest(

        @Schema(description = "Nueva cantidad deseada (0 para eliminar el producto, máximo 10)", example = "5")
        @PositiveOrZero(message = "La cantidad no puede ser negativa")
        @Max(value = 10, message = "Máximo 10 unidades permitidas por producto")
        int nuevaCantidad

) {}