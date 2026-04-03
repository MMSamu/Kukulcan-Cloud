package com.uamishop.backend.shared.event;

import java.time.Instant;
import java.util.UUID;

public record CarritoFinalizadoEvent(
    UUID eventId,
    Instant occurredAt,
    UUID carritoId,
    UUID clienteId,
    String calle,
    String numero,
    String codigoPostal,
    String ciudad,
    String estado,
    String telefono
) {}