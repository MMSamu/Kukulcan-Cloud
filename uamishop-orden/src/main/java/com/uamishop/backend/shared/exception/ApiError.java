package com.uamishop.backend.shared.exception;

/**
 * * @brief Estructura de la respuesta que se envía cuando ocurre un error en la API.
 * * @note Lau, el profe pide esto en el Paso 3. Así, cuando el frontend la riegue,
 * le mandamos un JSON con el código HTTP y el mensaje de por qué falló.
 */
public record ApiError(
        int status,
        String error,
        String message,
        String path
) {}