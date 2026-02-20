package com.uamishop.backend.ventas.domain;

// Estado del carrito de compras
public enum EstadoCarrito {
    ACTIVO,         // Se pueden agregar productos
    EN_CHECKOUT,    // Se esta pagando (bloqueado)
    COMPLETADO,     // Compra finalizada
    ABANDONADO      // Se dejo olvidado
}