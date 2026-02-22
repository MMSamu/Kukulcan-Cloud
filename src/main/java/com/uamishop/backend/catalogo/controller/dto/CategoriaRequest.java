package com.uamishop.backend.catalogo.controller.dto;


import jakarta.validation.constraints.*;

public record CategoriaRequest(

        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @NotBlank(message = "La descripción no puede estar vacía")
        @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
        String descripcion
) {}

