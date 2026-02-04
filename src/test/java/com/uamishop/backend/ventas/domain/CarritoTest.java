package com.uamishop.backend.ventas.domain;

import com.uamishop.backend.shared.domain.Money;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class CarritoTest {

    @Test
    void deberiaAgregarProductoNuevo() {
        // Preparación
        UUID clienteId = UUID.randomUUID();
        Carrito carrito = new Carrito(clienteId);
        UUID productoId = UUID.randomUUID();
        Money precio = Money.pesos(100.00);

        // Ejecución
        carrito.agregarProducto(productoId, 1, precio);

        // Verificación
        assertEquals(1, carrito.getItems().size());
        assertEquals(1, carrito.getItems().get(0).getCantidad());
    }

    @Test
    void deberiaSumarCantidadSiProductoYaExiste() {
        // RN-VEN-04: Si ya existe, se suma
        UUID clienteId = UUID.randomUUID();
        Carrito carrito = new Carrito(clienteId);
        UUID productoId = UUID.randomUUID();
        Money precio = Money.pesos(50.00);

        carrito.agregarProducto(productoId, 2, precio); // Agregamos 2
        carrito.agregarProducto(productoId, 3, precio); // Agregamos 3 más

        assertEquals(1, carrito.getItems().size()); // Sigue siendo 1 item
        assertEquals(5, carrito.getItems().get(0).getCantidad()); // Total 5
    }

    @Test
    void noDeberiaPermitirMasDe10UnidadesPorProducto() {
        // RN-VEN-02: Máximo 10 unidades
        UUID clienteId = UUID.randomUUID();
        Carrito carrito = new Carrito(clienteId);
        UUID productoId = UUID.randomUUID();
        Money precio = Money.pesos(10.00);

        // Intentamos agregar 11 de golpe
        Exception exception = assertThrows(RuntimeException.class, () -> {
            carrito.agregarProducto(productoId, 11, precio);
        });

        assertEquals("Maximo 10 unidades permitidas por producto", exception.getMessage());
    }

    @Test
    void noDeberiaPermitirAgregarSiCantidadEsCeroONegativa() {
        // RN-VEN-01: Cantidad mayor a cero
        Carrito carrito = new Carrito(UUID.randomUUID());
        UUID productoId = UUID.randomUUID();
        Money precio = Money.pesos(10.00);

        assertThrows(RuntimeException.class, () -> {
            carrito.agregarProducto(productoId, 0, precio);
        });
    }
}