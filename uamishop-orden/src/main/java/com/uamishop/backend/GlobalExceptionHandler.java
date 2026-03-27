package com.uamishop.backend;

import com.uamishop.backend.shared.exception.ApiError;
import com.uamishop.backend.shared.exception.BusinessRuleException;
import com.uamishop.backend.shared.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * @class GlobalExceptionHandler
 *
 * @brief Manejador global de excepciones para toda la API REST.
 *
 * @details
 * Esta clase intercepta todas las excepciones lanzadas por los controladores
 * (@RestController) y las transforma en respuestas HTTP estructuradas en formato JSON.
 *
 * Gracias a @RestControllerAdvice:
 * - No es necesario usar bloques try-catch en los controllers.
 * - Se centraliza el manejo de errores.
 * - Se garantiza consistencia en las respuestas de error.
 *
 * Beneficios arquitectónicos:
 * ✔ Separación de responsabilidades.
 * ✔ Código más limpio y mantenible.
 * ✔ API REST profesional y estandarizada.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @brief Maneja excepciones de reglas de negocio.
     *
     * @details
     * Se lanza cuando se viola una regla del dominio.
     * Ejemplos:
     * - Carrito lleno.
     * - Stock insuficiente.
     * - Operación no permitida por estado.
     *
     * HTTP 422 - Unprocessable Entity
     *
     * @param ex excepción personalizada de negocio.
     * @param request información de la petición HTTP.
     * @return ResponseEntity con estructura ApiError.
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiError> handleBusinessRuleException(
            BusinessRuleException ex,
            WebRequest request
    ) {

        ApiError apiError = new ApiError(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Unprocessable Entity - Regla de negocio violada",
                ex.getMessage(),
                getPath(request)
        );

        return new ResponseEntity<>(apiError, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * @brief Maneja errores de validación en DTOs.
     *
     * @details
     * Se activa automáticamente cuando fallan anotaciones como:
     * - @NotNull
     * - @NotBlank
     * - @Positive
     * - @Size
     *
     * Spring lanza MethodArgumentNotValidException cuando un @Valid falla.
     *
     * HTTP 400 - Bad Request
     *
     * @param ex excepción de validación.
     * @param request información de la petición HTTP.
     * @return ResponseEntity con detalle del primer error detectado.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {

        // Se obtiene el primer error de validación del DTO
        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request - Error de validación",
                errorMessage,
                getPath(request)
        );

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     * @brief Maneja cuando un recurso no es encontrado.
     *
     * @details
     * Se utiliza cuando:
     * - No existe un producto.
     * - No existe un carrito.
     * - No existe una categoría.
     *
     * HTTP 404 - Not Found
     *
     * @param ex excepción personalizada de recurso no encontrado.
     * @param request información de la petición HTTP.
     * @return ResponseEntity con detalles del error.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request
    ) {

        ApiError apiError = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                getPath(request)
        );

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    /**
     * @brief Maneja conflictos de estado.
     *
     * @details
     * Se produce cuando el estado actual del recurso
     * no permite la operación solicitada.
     *
     * Ejemplos:
     * - Modificar un carrito COMPLETADO.
     * - Activar algo ya activo.
     *
     * HTTP 409 - Conflict
     *
     * @param ex excepción de estado inválido.
     * @param request información de la petición HTTP.
     * @return ResponseEntity con detalles del conflicto.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleConflictException(
            IllegalStateException ex,
            WebRequest request
    ) {

        ApiError apiError = new ApiError(
                HttpStatus.CONFLICT.value(),
                "Conflict - Estado no válido para la operación",
                ex.getMessage(),
                getPath(request)
        );

        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    /**
     * @brief Maneja cualquier excepción no controlada.
     *
     * @details
     * Es el último nivel de captura de errores.
     * Atrapa fallos inesperados como:
     * - NullPointerException
     * - Errores de base de datos
     * - Fallos de infraestructura
     *
     * HTTP 500 - Internal Server Error
     *
     * @param ex excepción genérica.
     * @param request información de la petición HTTP.
     * @return ResponseEntity con mensaje genérico.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(
            Exception ex,
            WebRequest request
    ) {

        // Se imprime en consola para depuración interna
        ex.printStackTrace();

        ApiError apiError = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ocurrió un error inesperado en el servidor",
                getPath(request)
        );

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * @brief Extrae la ruta (URI) donde ocurrió el error.
     *
     * @details
     * Permite incluir en la respuesta JSON la URL exacta
     * que generó la excepción.
     *
     * @param request petición HTTP actual.
     * @return URI solicitada o "Desconocido" si no puede determinarse.
     */
    private String getPath(WebRequest request) {

        if (request instanceof ServletWebRequest) {
            return ((ServletWebRequest) request)
                    .getRequest()
                    .getRequestURI();
        }

        return "Desconocido";
    }
}