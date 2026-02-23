/**
 * @file ProductoResponse.java
 * @brief DTO (Data Transfer Object) que representa la respuesta de un producto.
 *
 * Este record encapsula la información que el backend envía al cliente
 * cuando se consulta o registra un producto.
 *
 * No contiene validaciones porque no recibe datos, solo los expone.
 */
package com.uamishop.backend.catalogo.controller.dto;

import java.math.BigDecimal; // Clase para manejar números decimales con alta precisión (ideal para dinero)
import java.util.UUID;       // Clase para representar identificadores únicos universales

/**
 * @record ProductoResponse
 * @brief Representa los datos que se devuelven al cliente sobre un producto.
 *
 * Este DTO es inmutable y generalmente se construye a partir de una entidad
 * de base de datos antes de enviarse como respuesta JSON en una API REST.
 *
 * @param id Identificador único del producto
 * @param nombre Nombre del producto
 * @param descripcion Descripción del producto
 * @param precio Precio del producto
 * @param categoriaId Identificador único de la categoría asociada
 * @param activo Indica si el producto está activo o disponible
 */
public record ProductoResponse(

        /**
         * Identificador único del producto.
         * Generalmente corresponde al ID almacenado en la base de datos.
         */
        UUID id,

        /**
         * Nombre del producto.
         */
        String nombre,

        /**
         * Descripción detallada del producto.
         */
        String descripcion,

        /**
         * Precio del producto.
         * Se utiliza BigDecimal para evitar problemas de precisión en valores monetarios.
         */
        BigDecimal precio,

        /**
         * Identificador único de la categoría a la que pertenece el producto.
         */
        UUID categoriaId,

        /**
         * Indica si el producto está activo.
         * true  -> Producto disponible
         * false -> Producto deshabilitado o eliminado lógicamente
         */
        boolean activo

) {}


