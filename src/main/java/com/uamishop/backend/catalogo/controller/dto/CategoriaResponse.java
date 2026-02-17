package com.uamishop.backend.catalogo.controller.dto;

import java.util.UUID;

public record CategoriaResponse(
        UUID id,
        String nombre,
        String descripcion
){}
