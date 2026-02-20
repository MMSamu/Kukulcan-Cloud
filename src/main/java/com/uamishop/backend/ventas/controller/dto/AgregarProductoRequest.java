package com.uamishop.backend.ventas.controller.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Este Record actúa como un "Contenedor de Datos de Entrada".
 * Se usa cuando el Frontend envía información para agregar un producto.
 */
public record AgregarProductoRequest(

    // Validamos que el cliente envíe forzosamente el ID del producto
    @NotNull(message = "El ID del producto es obligatorio")
    UUID productoId,

    // Validamos que la cantidad sea un número entero positivo y no excesivo
    @Positive(message = "La cantidad debe ser mayor a cero")
    @Max(value = 10, message = "Máximo 10 unidades permitidas por producto")
    int cantidad,
    
    // Validamos que el precio sea un número decimal positivo y razonable
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
    BigDecimal precioMonto
) {}