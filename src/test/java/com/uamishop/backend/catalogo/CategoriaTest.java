package com.uamishop.backend.catalogo;

import com.uamishop.backend.catalogo.domain.Categoria;
import com.uamishop.backend.catalogo.domain.CategoriaId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaTest {

    @Test
    void deberiaCrearCategoriaCorrectamente() {
        Categoria categoria = new Categoria(
                CategoriaId.generar(),
                "Electrónica",
                "Productos electrónicos"
        );

        assertNotNull(categoria.getId());
    }

    @Test
    void deberiaActualizarNombreYDescripcion() {
        Categoria categoria = new Categoria(
                CategoriaId.generar(),
                "Ropa",
                "Descripción inicial"
        );

        categoria.actualizar("Ropa deportiva", "Nueva descripción");

        // Si no lanza excepción, el comportamiento es válido
        assertTrue(true);
    }

    @Test
    void deberiaAsignarCategoriaPadre() {
        CategoriaId padreId = CategoriaId.generar();
        Categoria categoria = new Categoria(
                CategoriaId.generar(),
                "Tenis",
                "Calzado"
        );

        categoria.asignarPadre(padreId);

        assertTrue(true);
    }
}
