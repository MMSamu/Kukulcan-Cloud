package com.uamishop.backend.orden.domain;

import com.uamishop.backend.shared.domain.Money;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrdenTest {

    @Test
    void testAplicarDescuentoMontoFijo() {
        Orden orden = new Orden(UUID.randomUUID(),
                DireccionEnvio.crear("Calle 1", "Ciudad 1", "Estado 1", "12345", "5512345678"));
        ItemOrden item = ItemOrden.crear(UUID.randomUUID(), "Producto 1", "SKU1", 1, Money.pesos(100));
        orden.agregarItem(item);

        // Subtotal = 100
        assertEquals(Money.pesos(100).getCantidad().doubleValue(),
                orden.calcularSubtotal().getCantidad().doubleValue());

        // Aplicar descuento de 10
        orden.aplicarDescuento(Money.pesos(10));

        assertEquals(Money.pesos(10).getCantidad().doubleValue(), orden.getDescuento().getCantidad().doubleValue());
        assertEquals(Money.pesos(90).getCantidad().doubleValue(), orden.getTotal().getCantidad().doubleValue());
    }

    @Test
    void testAplicarDescuentoPorcentaje() {
        Orden orden = new Orden(UUID.randomUUID(),
                DireccionEnvio.crear("Calle 1", "Ciudad 1", "Estado 1", "12345", "5512345678"));
        ItemOrden item = ItemOrden.crear(UUID.randomUUID(), "Producto 1", "SKU1", 1, Money.pesos(200));
        orden.agregarItem(item);

        // Subtotal = 200
        // Aplicar 10% -> 20
        orden.aplicarDescuento(10.0);

        assertEquals(Money.pesos(20.0).getCantidad().doubleValue(), orden.getDescuento().getCantidad().doubleValue());
        assertEquals(Money.pesos(180.0).getCantidad().doubleValue(), orden.getTotal().getCantidad().doubleValue());
    }

    @Test
    void testDescuentoMayorQueSubtotal() {
        Orden orden = new Orden(UUID.randomUUID(),
                DireccionEnvio.crear("Calle 1", "Ciudad 1", "Estado 1", "12345", "5512345678"));
        ItemOrden item = ItemOrden.crear(UUID.randomUUID(), "Producto 1", "SKU1", 1, Money.pesos(50));
        orden.agregarItem(item);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orden.aplicarDescuento(Money.pesos(60));
        });

        assertEquals("El descuento no puede ser mayor al subtotal", exception.getMessage());
    }

    @Test
    void testPorcentajeInvalido() {
        Orden orden = new Orden(UUID.randomUUID(),
                DireccionEnvio.crear("Calle 1", "Ciudad 1", "Estado 1", "12345", "5512345678"));

        assertThrows(IllegalArgumentException.class, () -> {
            orden.aplicarDescuento(-5.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            orden.aplicarDescuento(105.0);
        });
    }
}
