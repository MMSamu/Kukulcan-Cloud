package com.uamishop.catalogo.api;

import java.util.UUID;

public record CategoriaResumen(
        UUID categoriaId,
        String nombre,
        String descripcion
) {}
