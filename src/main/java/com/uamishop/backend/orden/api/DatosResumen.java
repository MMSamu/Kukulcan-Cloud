// DTO para representar el resumen de datos de una orden
// Se utiliza para devolver los datos de una orden de forma resumida

package com.uamishop.backend.orden.api;

import java.util.UUID;

public record DatosResumen(
                UUID clienteId,
                String nombre,
                String apellido,
                String estado,
                String direccion,
                String telefono,
                String metodoPago,
                String formaPago,
                String fecha,
                String hora) {

}
