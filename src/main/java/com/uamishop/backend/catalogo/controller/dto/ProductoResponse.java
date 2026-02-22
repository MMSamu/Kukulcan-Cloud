package com.uamishop.backend.catalogo.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record  ProductoResponse (
        UUID id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        UUID categoriaId,
        boolean activo
) {}



