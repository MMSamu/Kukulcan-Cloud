package com.uamishop.shared.event;

import java.math.BigDecimal;
import java.util.UUID;

public record CarritoFinalizadoEvent(
    UUID clienteId,
    String calle,
    String numero,
    String ciudad,
    String estado,
    String codigoPostal,
    String telefono,
    BigDecimal total
) {}