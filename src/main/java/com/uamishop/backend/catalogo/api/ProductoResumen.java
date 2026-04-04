package com.uamishop.backend.catalogo.api;

import com.uamishop.backend.shared.domain.Money;
import java.util.UUID;

public record ProductoResumen(
        UUID productoId,
        String nombre,
        String descripcion,
        Money precio,
        boolean disponible
) {}