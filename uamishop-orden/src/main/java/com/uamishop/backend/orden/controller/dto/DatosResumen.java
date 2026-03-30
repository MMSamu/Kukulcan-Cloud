// DTO para representar el resumen de datos de una orden
// Se utiliza para devolver los datos de una orden de forma resumida

package com.uamishop.backend.orden.controller.dto;

import java.util.UUID;

public record DatosResumen(
    UUID clienteId,
    String nombreCliente,
    String emailCliente,
    String estadoOrden,
    String direccion,
    String telefono,
    String metodoPago,
    String referenciaPago,
    String fecha,
    String hora
) {}
