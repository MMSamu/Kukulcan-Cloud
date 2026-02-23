/**
 * @file CategoriaResponse.java
 * @brief DTO (Data Transfer Object) que representa la respuesta de una categoría.
 *
 * Este record se utiliza para enviar información desde el backend hacia el cliente.
 * No contiene validaciones porque no recibe datos, solo los expone.
 */
package com.uamishop.backend.catalogo.controller.dto;

import java.util.UUID; // Clase que representa identificadores únicos universales

/**
 * @record CategoriaResponse
 * @brief Representa los datos que se devuelven al cliente sobre una categoría.
 *
 * Este DTO es inmutable y se usa comúnmente para transformar una entidad
 * de base de datos en un formato seguro y controlado para la API.
 *
 * @param id Identificador único de la categoría
 * @param nombre Nombre de la categoría
 * @param descripcion Descripción de la categoría
 */
public record CategoriaResponse(

        /**
         * Identificador único de la categoría.
         * Generalmente corresponde al ID almacenado en la base de datos.
         */
        UUID id,

        /**
         * Nombre de la categoría.
         */
        String nombre,

        /**
         * Descripción de la categoría.
         */
        String descripcion

){}
