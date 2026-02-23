/**
 * @file ProductoRequest.java
 * @brief DTO (Data Transfer Object) que representa la solicitud para crear o actualizar un producto.
 *
 * Esta clase utiliza un record de Java para encapsular los datos que llegan
 * desde el cliente (por ejemplo, desde una API REST).
 *
 * Incluye validaciones mediante anotaciones de Jakarta Validation.
 */
package com.uamishop.backend.catalogo.controller.dto;

import jakarta.validation.constraints.*; // Importa anotaciones de validación (@NotBlank, @Size, etc.)
import java.math.BigDecimal;             // Clase para manejar valores decimales con precisión (ideal para dinero)
import java.util.UUID;                   // Clase para representar identificadores únicos universales

/**
 * @record ProductoRequest
 * @brief Representa los datos necesarios para registrar o actualizar un producto.
 *
 * Este record contiene validaciones automáticas que serán evaluadas
 * antes de que el controlador procese la información.
 *
 * @param nombre Nombre del producto (no puede estar vacío y máximo 100 caracteres)
 * @param descripcion Descripción del producto (no puede estar vacía y máximo 255 caracteres)
 * @param precio Precio del producto (obligatorio y mayor a 0)
 * @param categoriaId Identificador único de la categoría a la que pertenece el producto
 */
public record ProductoRequest(

        /**
         * Nombre del producto.
         * No puede ser nulo ni vacío y tiene un máximo de 100 caracteres.
         */
        @NotBlank(message = "El nombre no puede estar vacio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        /**
         * Descripción detallada del producto.
         * No puede ser nula ni vacía y tiene un máximo de 255 caracteres.
         */
        @NotBlank(message = "La descripcion no puede estar vacia")
        @Size(max = 255, message = "La descripcion no puede superar 255 caracteres")
        String descripcion,

        /**
         * Precio del producto.
         * Debe ser obligatorio y mayor a 0.
         * Se usa BigDecimal para evitar errores de precisión con números decimales.
         */
        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio deber ser mayor a 0")
        BigDecimal precio,

        /**
         * Identificador único de la categoría del producto.
         * No puede ser nulo.
         */
        @NotNull(message = "LA categoria es obligatoria")
        UUID categoriaId

) {}

