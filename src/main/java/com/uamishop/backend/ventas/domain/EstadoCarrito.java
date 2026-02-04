package com.uamishop.backend.ventas.domain;

public enum EstadoCarrito {
    ACTIVO,         // Se pueden agregar productos
    EN_CHECKOUT,    // Se esta pagando (bloqueado)
    COMPLETADO,     // Compra finalizada
    ABANDONADO      // Se dejo olvidado
}