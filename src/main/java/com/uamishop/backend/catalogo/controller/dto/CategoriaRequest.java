/**
 * @file CategoriaRequest.java
 * @brief DTO (Data Transfer Object) que representa la solicitud para crear o actualizar una categoría.
 *
 * Este record encapsula los datos enviados desde el cliente hacia el backend.
 * Incluye validaciones usando Jakarta Validation para garantizar integridad
 * antes de procesar la información en el controlador.
 */
package com.uamishop.backend.catalogo.controller.dto;

import jakarta.validation.constraints.*; // Importa anotaciones de validación como @NotBlank y @Size

/**
 * @record CategoriaRequest
 * @brief Representa los datos necesarios para registrar o actualizar una categoría.
 *
 * Este DTO es inmutable y se utiliza comúnmente en controladores REST
 * junto con la anotación @Valid para activar las validaciones automáticas.
 *
 * @param nombre Nombre de la categoría (no puede estar vacío y máximo 100 caracteres)
 * @param descripcion Descripción de la categoría (no puede estar vacía y máximo 255 caracteres)
 */
public record CategoriaRequest(

        /**
         * Nombre de la categoría.
         * No puede ser nulo, vacío ni contener solo espacios.
         * Máximo 100 caracteres.
         */
        @NotBlank(message = "El nombre no puede estar vacío")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        /**
         * Descripción detallada de la categoría.
         * No puede ser nula, vacía ni contener solo espacios.
         * Máximo 255 caracteres.
         */
        @NotBlank(message = "La descripción no puede estar vacía")
        @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
        String descripcion

) {}

