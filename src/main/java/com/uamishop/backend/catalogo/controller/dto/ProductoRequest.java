package com.uamishop.backend.catalogo.controller.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;

    public record ProductoRequest(
            @NotBlank(message = "El nombre no puede estar vacio")
            @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
            String nombre,

            @NotBlank(message = "La descripcion no puede estar vacia")
            @Size(max = 255, message = "La descripcion no puede superar 255 caracteres")
            String descripcion,

            @NotNull(message = "El precio es obligatorio")
            @Positive(message = "El precio deber ser mayor a 0")
            BigDecimal precio,

            @NotNull(message = "LA categoria es obligatoria")
            UUID categoriaId

    ) {}

