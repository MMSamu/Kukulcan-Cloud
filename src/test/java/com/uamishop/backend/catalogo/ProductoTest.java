package com.uamishop.backend.catalogo;

import com.uamishop.backend.catalogo.domain.CategoriaId;
import com.uamishop.backend.catalogo.domain.Producto;
import com.uamishop.backend.shared.domain.Money;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {

    @Test
    void deberiaCrearProductoValido() {
        // Arrange
        Money precio = Money.pesos(15000);
        CategoriaId categoriaId = CategoriaId.generar();

        // Act
        Producto producto = Producto.crear(
                "Laptop",
                "Laptop gamer",
                precio,
                categoriaId);

        // Assert
        assertNotNull(producto);
        assertEquals("Laptop", producto.getNombre());
        assertEquals(precio, producto.getPrecio());
        assertEquals(categoriaId, producto.getCategoriaId());
        assertFalse(producto.isDisponible());
    }

    @Test
    void noDeberiaCrearProductoConPrecioCero() {
        // Arrange
        Money precio = Money.pesos(0);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> Producto.crear(
                "Mouse",
                "Mouse gamer",
                precio,
                CategoriaId.generar()));
    }
}
