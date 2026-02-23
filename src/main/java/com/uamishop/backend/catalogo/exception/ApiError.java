package com.uamishop.backend.catalogo.exception;

import java.time.LocalDateTime;

/**
 * @file ApiError.java
 * @brief Clase que representa la estructura estándar de errores de la API.
 *
 * Se utiliza para devolver respuestas estructuradas cuando ocurre
 * una excepción en el módulo catálogo.
 *
 * @author Sebastian
 * @version 1.0
 * @since 1.0
 */
public class ApiError {

    /**
     * @brief Código HTTP del error.
     */
    private int status;

    /**
     * @brief Nombre corto del error.
     */
    private String error;

    /**
     * @brief Mensaje descriptivo del error.
     */
    private String message;

    /**
     * @brief Ruta donde ocurrió el error.
     */
    private String path;

    /**
     * @brief Fecha y hora del error.
     */
    private LocalDateTime timestamp;

    /**
     * @brief Constructor principal de ApiError.
     *
     * @param status Código HTTP
     * @param error Tipo de error
     * @param message Mensaje descriptivo
     * @param path Endpoint donde ocurrió
     */
    public ApiError(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
