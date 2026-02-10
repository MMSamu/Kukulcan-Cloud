package com.uamishop.backend.catalogo.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImagenTest {

    @Test
    void deberiaCrearImagenValida() {
        Imagen imagen = new Imagen(
                "https://imagenes.com/img.png",
                "Imagen frontal",
                0
        );

        assertEquals(0, imagen.getOrden());
    }

    @Test
    void noDeberiaPermitirUrlVacia() {
        assertThrows(IllegalArgumentException.class, () ->
                new Imagen("", "alt", 0)
        );
    }

    @Test
    void noDeberiaPermitirOrdenNegativo() {
        assertThrows(IllegalArgumentException.class, () ->
                new Imagen("https://img.com/a.png", "alt", -1)
        );
    }
}
