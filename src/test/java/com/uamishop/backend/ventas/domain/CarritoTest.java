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
        carrito.agregarProducto(productoId, 3, precio); // Agregamos 3 mas

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

    @Test
    void deberiaEliminarProducto() {
        // Preparacion
        Carrito carrito = new Carrito(UUID.randomUUID());
        UUID productoId = UUID.randomUUID();
        carrito.agregarProducto(productoId, 2, Money.pesos(50.0));

        // Accion
        carrito.eliminarProducto(productoId);

        // Verificacion
        assertEquals(0, carrito.getItems().size(), "El carrito deberia estar vacio");
    }

    @Test
    void deberiaModificarCantidadCorrectamente() {
        Carrito carrito = new Carrito(UUID.randomUUID());
        UUID productoId = UUID.randomUUID();
        carrito.agregarProducto(productoId, 1, Money.pesos(100.0));

        // Cambiamos de 1 a 5 unidades
        carrito.modificarCantidad(productoId, 5);

        assertEquals(5, carrito.getItems().get(0).getCantidad());
    }

    @Test
    void deberiaVaciarElCarrito() {
        Carrito carrito = new Carrito(UUID.randomUUID());
        carrito.agregarProducto(UUID.randomUUID(), 1, Money.pesos(10.0));
        carrito.agregarProducto(UUID.randomUUID(), 1, Money.pesos(20.0));

        carrito.vaciar();

        assertTrue(carrito.getItems().isEmpty(), "La lista de items deberia estar vacia");
    }

    @Test
    void deberiaCalcularTotalConDescuento() {
        // Producto de $100 x 2 unidades = $200
        Carrito carrito = new Carrito(UUID.randomUUID());
        carrito.agregarProducto(UUID.randomUUID(), 2, Money.pesos(100.0));

        // Aplicamos descuento de $50
        carrito.aplicarDescuento(Money.pesos(50.0));

        // Total esperado: 200 - 50 = 150
        assertEquals(150.0, carrito.calcularTotal().getCantidad().doubleValue());
    }

    @Test
    void noDeberiaPermitirDescuentoMayorAlTotal() {
        Carrito carrito = new Carrito(UUID.randomUUID());
        carrito.agregarProducto(UUID.randomUUID(), 1, Money.pesos(100.0));

        // Intentamos descontar $200 a una compra de $100
        assertThrows(RuntimeException.class, () -> {
            carrito.aplicarDescuento(Money.pesos(200.0));
        });
    }

    @Test
    void deberiaBloquearseAlHacerCheckout() {
        Carrito carrito = new Carrito(UUID.randomUUID());
        carrito.agregarProducto(UUID.randomUUID(), 1, Money.pesos(100.0));

        carrito.iniciarCheckout();

        assertEquals(EstadoCarrito.EN_CHECKOUT, carrito.getEstado());

        // Intentar agregar algo mas deberia fallar
        assertThrows(RuntimeException.class, () -> {
            carrito.agregarProducto(UUID.randomUUID(), 1, Money.pesos(50.0));
        });
    }

    @Test
    void noDeberiaPermitirCheckoutSiTotalEsMenorOIgualA50() {
        // RN-VEN-12: Total debe ser mayor a $50 pesos
        Carrito carrito = new Carrito(UUID.randomUUID());
        // Agregamos algo que sume $40 (Menor al límite de $50)
        carrito.agregarProducto(UUID.randomUUID(), 1, Money.pesos(40.0));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            carrito.iniciarCheckout();
        });
        
        assertTrue(exception.getMessage().contains("monto minimo"),
            "Deberia lanzar error por monto minimo de compra");
    }

    @Test
    void noDeberiaPermitirAbandonarSiNoEstaEnCheckout() {
        // RN-VEN-14: Solo se puede abandonar si el estado es EN_CHECKOUT
        Carrito carrito = new Carrito(UUID.randomUUID());
        
        // El carrito está ACTIVO por defecto, no debería dejar abandonarlo
        assertThrows(RuntimeException.class, () -> {
            carrito.abandonar();
        }, "No debería permitir abandonar un carrito que no está en checkout");
    }

    @Test
    void deberiaPermitirAbandonarSiEstaEnCheckout() {
        // RN-VEN-14: Flujo correcto de abandono
        Carrito carrito = new Carrito(UUID.randomUUID());
        carrito.agregarProducto(UUID.randomUUID(), 1, Money.pesos(100.0));
        
        carrito.iniciarCheckout(); // Cambia a EN_CHECKOUT
        carrito.abandonar();      // Ahora sí es válido

        assertEquals(EstadoCarrito.ABANDONADO, carrito.getEstado());
    }

    @Test
    void noDeberiaPermitirDescuentoMayorAl30PorCiento() {
        // RN-VEN-16: El descuento no puede superar el 30% del subtotal
        Carrito carrito = new Carrito(UUID.randomUUID());
        // Compra de $100.00
        carrito.agregarProducto(UUID.randomUUID(), 1, Money.pesos(100.0)); 

        // El 30% es $30.0. Intentamos aplicar $31.0 (debería fallar)
        Money descuentoInvalido = Money.pesos(31.0);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            carrito.aplicarDescuento(descuentoInvalido);
        });

        assertEquals("El descuento no puede ser mayor al 30% del subtotal", exception.getMessage());
    }
}