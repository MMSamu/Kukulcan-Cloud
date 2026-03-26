package com.uamishop.ventas.api;

import java.util.UUID;

// Define los métodos públicos de la API expuestos a otros módulos
public interface VentasApi {
    CarritoResumen obtenerResumen(UUID carritoId);
    void completarCheckoutPublico(UUID carritoId);
}


