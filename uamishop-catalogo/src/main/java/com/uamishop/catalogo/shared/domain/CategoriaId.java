package com.uamishop.catalogo.shared.domain;



// Importa la clase UUID para generar identificadores únicos
import java.util.UUID;

/**
 * @record CategoriaId
 * @brief Value Object que envuelve un UUID como identificador de categoría.
 *
 * En lugar de usar UUID directamente en el dominio,
 * se crea este tipo fuerte para mejorar:
 * - Seguridad de tipos
 * - Expresividad del modelo
 * - Encapsulamiento
 *
 * @param valor UUID interno que representa el identificador
 */
public record CategoriaId(UUID valor) {

    /**
     * Constructor compacto del record.
     *
     * Se ejecuta automáticamente cuando se crea una instancia.
     * Permite validar el valor antes de asignarlo.
     *
     * @throws IllegalArgumentException si el UUID es nulo
     */
    public CategoriaId {

        // Regla de negocio:
        // El identificador no puede ser nulo
        if (valor == null) {
            throw new IllegalArgumentException("El id de categoría no puede ser nulo");
        }
    }

    /**
     * Método estático de fábrica para generar un nuevo identificador.
     *
     * @return Nueva instancia de CategoriaId con UUID aleatorio
     */
    public static CategoriaId generar() {

        // UUID.randomUUID() genera un identificador único universal
        return new CategoriaId(UUID.randomUUID());
    }
}
