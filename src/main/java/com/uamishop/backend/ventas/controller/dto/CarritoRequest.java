package com.uamishop.backend.ventas.controller.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/* Este Record actúa como un "Contenedor de Datos de Entrada".
 * Se usa cuando el Frontend envía información para crear un carrito.
 */
public record CarritoRequest(
    // Validamos que el clienteId no sea nulo, ya que es necesario para crear un carrito asociado 
    // a un cliente específico.
    @NotNull(message = "El ID del cliente es obligatorio")
    UUID clienteId
) {}