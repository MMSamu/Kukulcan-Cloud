package com.uamishop.backend;

import com.uamishop.backend.shared.exception.ApiError;
import com.uamishop.backend.shared.exception.BusinessRuleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * * @brief Interceptor global de excepciones para toda la aplicación.
 * * @note Con @RestControllerAdvice ya no tenemos que poner try-catch en el
 * CarritoController. Si las validaciones del DTO fallan, esto manda un 400.
 * Si una regla de negocio de Ventas falla, esto manda un 422.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * * @brief Maneja las excepciones cuando el usuario viola una regla de negocio (ej. Carrito Lleno)
     * HTTP 422 Unprocessable Entity
     */

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiError> handleBusinessRuleException(BusinessRuleException ex, WebRequest request) {
        ApiError apiError = new ApiError(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Unprocessable Entity - Regla de negocio violada",
                ex.getMessage(),
                getPath(request)
        );
        return new ResponseEntity<>(apiError, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * * @brief Maneja los errores de validación de los DTOs (ej. @NotNull, @Positive fallando)
     * HTTP 400 Bad Request
     */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        // Extraemos el primer mensaje de error de nuestras validaciones del DTO
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request - Error de validación",
                errorMessage,
                getPath(request)
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     * * @brief Extrae la ruta (URL) donde ocurrió el error para ponerla en la respuesta.
     */

    private String getPath(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            return ((ServletWebRequest) request).getRequest().getRequestURI();
        }
        return "Desconocido";
    }
}