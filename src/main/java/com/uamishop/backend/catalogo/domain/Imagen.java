package com.uamishop.backend.catalogo.domain;

import java.util.Objects;

public class Imagen {

    private final String url;
    private final String textoAlternativo;
    private final int orden;

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

    public String getUrl() {
        return url;
    }

    public String getTextoAlternativo() {
        return textoAlternativo;
    }

    public int getOrden() {
        return orden;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Imagen)) return false;
        Imagen imagen = (Imagen) o;
        return orden == imagen.orden &&
                url.equals(imagen.url) &&
                Objects.equals(textoAlternativo, imagen.textoAlternativo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, textoAlternativo, orden);
    }


}
