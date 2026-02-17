package com.uamishop.backend.catalogo.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

    public record ProductoRequest(
            String nombre,
            String descripcion,
            BigDecimal precio,
            UUID categoriaId

    ) {}

