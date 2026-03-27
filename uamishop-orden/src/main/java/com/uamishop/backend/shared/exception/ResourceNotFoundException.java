package com.uamishop.backend.shared.exception;

import java.io.Serial;

/**
 * * @brief Excepción para recursos no encontrados (Error 404).
 */

public class ResourceNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }
}