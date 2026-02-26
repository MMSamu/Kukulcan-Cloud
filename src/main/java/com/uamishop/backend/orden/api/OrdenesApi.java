// API para manejar las operaciones relacionadas con las ordenes
// Se encarga de la comunicación entre la capa de presentación y la capa de negocio
// Implementa la interface OrdenesApi

package com.uamishop.backend.orden.api;

import java.util.List;
import java.util.UUID;

public interface OrdenesApi {

    // Obtiene un resumen de una orden específica
    OrdenResumen obtenerOrden(UUID ordenId);

    // Obtiene una lista de resúmenes de todas las ordenes
    List<OrdenResumen> listarProductos();

    // Obtiene una lista de resúmenes de datos
    List<DatosResumen> listarDatos();
}
