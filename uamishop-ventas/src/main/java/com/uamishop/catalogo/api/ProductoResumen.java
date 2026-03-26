package com.uamishop.catalogo.api;

import com.uamishop.shared.domain.Money;
import java.util.UUID;

public record ProductoResumen(
        UUID productoId,
        String nombre,
        String descripcion,
        Money precio,
        boolean disponible
) {}