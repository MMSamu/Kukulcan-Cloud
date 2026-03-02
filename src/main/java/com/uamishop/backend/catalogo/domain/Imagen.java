/**
 * @file Imagen.java
 * @brief Value Object que representa una imagen asociada a un producto.
 *
 * Pertenece a la capa Domain.
 * Sigue los principios de Domain Driven Design (DDD).
 *
 * Características:
 * - Es inmutable (sus atributos son final).
 * - Valida sus reglas de negocio en el constructor.
 * - Implementa equals y hashCode por valor.
 */
package com.uamishop.backend.catalogo.domain;

// Importación necesaria para comparar objetos de forma segura
import java.util.Objects;
import java.util.UUID;

/**
 * @class Imagen
 * @brief Value Object que representa una imagen del catálogo.
 *
 * Un Value Object:
 * - No tiene identidad propia
 * - Se compara por sus atributos
 * - Es inmutable
 *
 * Reglas de negocio:
 * - La URL no puede ser nula ni vacía.
 * - Debe iniciar con http:// o https://.
 * - El orden no puede ser negativo.
 */
public class Imagen {

    /**
     * URL pública donde se encuentra la imagen.
     * Es final para garantizar inmutabilidad.
     */
    private final String url;

    /**
     * Texto alternativo de la imagen.
     * Mejora accesibilidad (SEO y lectores de pantalla).
     */
    private final String textoAlternativo;

    /**
     * Orden de presentación dentro del producto.
     * Permite definir cuál imagen se muestra primero.
     */
    private final int orden;


    /**
     * Constructor principal del Value Object Imagen.
     *
     * Aquí se validan todas las reglas del dominio.
     * Si alguna regla falla, se lanza una excepción.
     *
     * @param url URL pública de la imagen
     * @param textoAlternativo descripción accesible
     * @param orden posición de visualización
     *
     * @throws IllegalArgumentException si:
     *         - La URL es nula o vacía
     *         - La URL no inicia con http:// o https://
     *         - El orden es negativo
     */
    public Imagen(String url, String textoAlternativo, int orden) {

        // Validación 1: URL obligatoria
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("La url no puede estar vacia");
        }

        // Validación 2: Debe ser una URL HTTP o HTTPS válida
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("La URL debe iniciar con http:// o https://");
        }

        // Validación 3: El orden no puede ser negativo
        if (orden < 0) {
            throw new IllegalArgumentException("El orden no puede ser negativo");
        }

        // Asignación de atributos (inmutables)
        this.url = url;
        this.textoAlternativo = textoAlternativo;
        this.orden = orden;
    }

    /**
     * Obtiene la URL de la imagen.
     *
     * @return URL de la imagen
     */
    public String getUrl() {
        return url;
    }

    /**
     * Obtiene el texto alternativo.
     *
     * @return texto alternativo descriptivo
     */
    public String getTextoAlternativo() {
        return textoAlternativo;
    }

    /**
     * Obtiene el orden de visualización.
     *
     * @return posición dentro del producto
     */
    public int getOrden() {
        return orden;
    }

    /**
     * Compara dos objetos Imagen por VALOR.
     *
     * En DDD, los Value Objects se comparan por sus atributos,
     * no por referencia de memoria.
     *
     * @param o objeto a comparar
     * @return true si todos los atributos coinciden
     */
    @Override
    public boolean equals(Object o) {

        // Si es el mismo objeto en memoria
        if (this == o) return true;

        // Si no es del mismo tipo
        if (!(o instanceof Imagen)) return false;

        // Cast seguro
        Imagen imagen = (Imagen) o;

        // Comparación por atributos
        return orden == imagen.orden &&
                url.equals(imagen.url) &&
                Objects.equals(textoAlternativo, imagen.textoAlternativo);
    }

    /**
     * Genera el hash basado en los atributos.
     *
     * Es obligatorio cuando se sobrescribe equals.
     *
     * Permite usar Imagen en:
     * - HashSet
     * - HashMap
     * - Colecciones basadas en hash
     *
     * @return valor hash consistente
     */
    @Override
    public int hashCode() {
        return Objects.hash(url, textoAlternativo, orden);
    }

    /**
     * @class ProductoId
     * @brief Value Object que encapsula el identificador del Producto.
     *
     * En DDD:
     * - Aunque identifica una entidad, sigue siendo un Value Object.
     * - Es inmutable.
     * - Se compara por valor.
     */
    public static class ProductoId {

        /** UUID interno que representa el identificador */
        private final UUID valor;

        /**
         * Constructor principal.
         *
         * Valida que el UUID no sea nulo.
         *
         * @param valor UUID del producto
         * @throws NullPointerException si el valor es null
         */
        public ProductoId(UUID valor) {

            // requireNonNull lanza NullPointerException si es null
            this.valor = Objects.requireNonNull(valor);
        }

        /**
         * Método fábrica para generar un nuevo identificador.
         *
         * @return nueva instancia de ProductoId con UUID aleatorio
         */
        public static ProductoId generar() {

            // UUID.randomUUID() crea un identificador único universal
            return new ProductoId(UUID.randomUUID());
        }


        /**
         * Obtiene el UUID encapsulado.
         *
         * @return valor interno UUID
         */
        public UUID valor() {
            return valor;
        }

        /**
         * Compara dos ProductoId por valor.
         *
         * Dos identificadores son iguales si contienen el mismo UUID.
         *
         * @param o objeto a comparar
         * @return true si representan el mismo ID
         */
        @Override
        public boolean equals(Object o) {

            if (this == o) return true;

            if (!(o instanceof ProductoId that)) return false;

            return valor.equals(that.valor);
        }

        /**
         * Genera el hash basado en el UUID.
         *
         * Obligatorio cuando se sobrescribe equals.
         *
         * @return hash del identificador
         */
        @Override
        public int hashCode() {
            return valor.hashCode();
        }

        /**
         * Representación en texto del identificador.
         *
         * Útil para logs, debugging y serialización.
         *
         * @return UUID en formato String
         */
        @Override
        public String toString() {
            return valor.toString();
        }

        public UUID getValue() {
            return valor;
        }
    }
}
