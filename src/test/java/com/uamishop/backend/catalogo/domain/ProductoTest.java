package com.uamishop.backend.catalogo.domain;

import com.uamishop.backend.shared.domain.Money;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del aggregate root Producto.
 *
 * Verifica el comportamiento del dominio del Catálogo
 * asegurando que las reglas de negocio se cumplan
 * durante la creación de productos.
 */
class ProductoTest {

    /**
     * Verifica que un producto se cree correctamente
     * cuando se proporcionan datos válidos.
     *
     * Reglas de negocio verificadas:
     * - RN-CAT-01: el nombre tiene longitud válida
     * - RN-CAT-02: el precio es mayor a cero
     * - RN-CAT-03: la descripción no excede el límite
     */
    @Test
    void deberiaCrearProductoConDatosValidos() {
        // Arrange
        Money precio = Money.pesos(150.0);
        CategoriaId categoriaId = CategoriaId.generar();

        // Act
        Producto producto = Producto.crear(
                "Carne de res",
                "Carne fresca de res",
                precio,
                categoriaId
        );

        // Assert
        assertNotNull(producto);
        assertEquals("Carne de res", producto.getNombre());
        assertEquals(precio, producto.getPrecio());
        assertEquals(categoriaId, producto.getCategoriaId());
    }
}