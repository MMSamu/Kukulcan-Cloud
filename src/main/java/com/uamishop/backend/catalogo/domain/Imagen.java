package com.uamishop.backend.catalogo.domain;

import java.util.Objects;

/**
 * Value Object Imagen.
 *
 * Representa una imagen asociada a un producto del catálogo.
 * Es un objeto inmutable que garantiza la validez de sus datos
 * desde el momento de su creación.
 *
 * Reglas de negocio:
 * - La URL no puede ser nula ni vacía.
 * - La URL debe iniciar con http:// o https://
 * - El orden no puede ser negativo.
 */
public class Imagen {

    /** URL de la imagen */
    private final String url;

    /** Texto alternativo para accesibilidad */
    private final String textoAlternativo;

    /** Orden de presentación de la imagen */
    private final int orden;


    /**
     * Constructor de la imagen.
     *
     * Valida todas las reglas de negocio al momento de crear la imagen.
     *
     * @param url URL pública de la imagen
     * @param textoAlternativo texto alternativo descriptivo
     * @param orden posición de la imagen dentro del producto
     *
     * @throws IllegalArgumentException si la URL es inválida o el orden es negativo
     */
    public Imagen(String url, String textoAlternativo, int orden) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("La url no puede estar vacia");
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException("La URL debe iniciar con http:// o https://");
        }
        if (orden < 0) {
            throw new IllegalArgumentException("El orden no puede ser negativo");
        }
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
     * Obtiene el texto alternativo de la imagen.
     *
     * @return texto alternativo
     */
    public String getTextoAlternativo() {
        return textoAlternativo;
    }

    /**
     * Obtiene el orden de la imagen.
     *
     * @return orden de presentación
     */
    public int getOrden() {
        return orden;
    }

    /**
     * Compara dos imágenes por valor.
     *
     * Dos imágenes son iguales si tienen la misma URL,
     * texto alternativo y orden.
     *
     * @param o objeto a comparar
     * @return true si son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Imagen)) return false;
        Imagen imagen = (Imagen) o;
        return orden == imagen.orden &&
                url.equals(imagen.url) &&
                Objects.equals(textoAlternativo, imagen.textoAlternativo);
    }

    /**
     * Genera el hash de la imagen basado en sus atributos.
     *
     * @return valor hash
     */
    @Override
    public int hashCode() {
        return Objects.hash(url, textoAlternativo, orden);
    }


}
