package com.uamishop.backend.ventas.domain;

import com.uamishop.backend.shared.domain.ClienteId;
import com.uamishop.backend.shared.domain.Money;
import com.uamishop.backend.shared.domain.ProductoId;
import com.uamishop.backend.shared.exception.DomainException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class CarritoTest {

    @Test
    @DisplayName("Debe agregar un producto nuevo al carrito")
    void deberiaAgregarProductoNuevo() {
        Carrito carrito = new Carrito(ClienteId.de(UUID.randomUUID()));
        ProductoId productoId = new ProductoId(UUID.randomUUID()); 
        Money precio = Money.pesos(100.00);

        carrito.agregarProducto(productoId, 1, precio);

        assertEquals(1, carrito.getItems().size());
        assertEquals(1, carrito.getItems().get(0).getCantidad());
    }

    @Test
    @DisplayName("Debe sumar la cantidad si el producto ya existe (RN-VEN-04)")
    void deberiaSumarCantidadSiProductoYaExiste() {
        Carrito carrito = new Carrito(ClienteId.de(UUID.randomUUID()));
        ProductoId productoId = new ProductoId(UUID.randomUUID());
        Money precio = Money.pesos(50.00);

        carrito.agregarProducto(productoId, 2, precio);
        carrito.agregarProducto(productoId, 3, precio);

        assertEquals(1, carrito.getItems().size());
        assertEquals(5, carrito.getItems().get(0).getCantidad());
    }

    @Test
    @DisplayName("No debe permitir más de 10 unidades por producto (RN-VEN-02)")
    void noDeberiaPermitirMasDe10UnidadesPorProducto() {
        Carrito carrito = new Carrito(ClienteId.de(UUID.randomUUID()));
        ProductoId productoId = new ProductoId(UUID.randomUUID());
        Money precio = Money.pesos(10.00);

        // SonarQube: Solo la llamada al método dentro de la lambda
        Exception exception = assertThrows(RuntimeException.class, () -> 
            carrito.agregarProducto(productoId, 11, precio)
        );

        assertEquals("Maximo 10 unidades permitidas por producto", exception.getMessage());
    }

    @Test
    @DisplayName("No debe permitir cantidad cero o negativa (RN-VEN-01)")
    void noDeberiaPermitirAgregarSiCantidadEsCeroONegativa() {
        Carrito carrito = new Carrito(ClienteId.de(UUID.randomUUID()));
        ProductoId productoId = new ProductoId(UUID.randomUUID());
        Money precio = Money.pesos(10.00);

        assertThrows(RuntimeException.class, () -> 
            carrito.agregarProducto(productoId, 0, precio)
        );
    }

    @Test
    @DisplayName("Debe eliminar un producto del carrito")
    void deberiaEliminarProducto() {
        Carrito carrito = new Carrito(ClienteId.de(UUID.randomUUID()));
        ProductoId productoId = new ProductoId(UUID.randomUUID());
        carrito.agregarProducto(productoId, 2, Money.pesos(50.0));

        carrito.eliminarProducto(productoId);

        assertEquals(0, carrito.getItems().size());
    }

    @Test
    @DisplayName("Debe modificar la cantidad de un producto existente")
    void deberiaModificarCantidadCorrectamente() {
        Carrito carrito = new Carrito(ClienteId.de(UUID.randomUUID()));
        ProductoId productoId = new ProductoId(UUID.randomUUID());
        carrito.agregarProducto(productoId, 1, Money.pesos(100.0));

        carrito.modificarCantidad(productoId, 5);

        assertEquals(5, carrito.getItems().get(0).getCantidad());
    }

    @Test
    @DisplayName("Debe vaciar todos los items del carrito")
    void deberiaVaciarElCarrito() {
        Carrito carrito = new Carrito(ClienteId.de(UUID.randomUUID()));
        carrito.agregarProducto(new ProductoId(UUID.randomUUID()), 1, Money.pesos(10.0));
        carrito.agregarProducto(new ProductoId(UUID.randomUUID()), 1, Money.pesos(20.0));

        carrito.vaciar();

        assertTrue(carrito.getItems().isEmpty());
    }

    @Test
    @DisplayName("Debe calcular el total aplicando el descuento")
    void deberiaCalcularTotalConDescuento() {
        Carrito carrito = new Carrito(ClienteId.de(UUID.randomUUID()));
        carrito.agregarProducto(new ProductoId(UUID.randomUUID()), 2, Money.pesos(100.0));

        carrito.aplicarDescuento(Money.pesos(50.0));

        assertEquals(150.0, carrito.calcularTotal().getCantidad().doubleValue());
    }

    @Test
    @DisplayName("No debe permitir descuentos mayores al total de la compra")
    void noDeberiaPermitirDescuentoMayorAlTotal() {
        Carrito carrito = new Carrito(ClienteId.de(UUID.randomUUID()));
        carrito.agregarProducto(new ProductoId(UUID.randomUUID()), 1, Money.pesos(100.0));
        
        // Declaramos la variable que faltaba
        Money descuentoExcedido = Money.pesos(200.0);

        // Corregimos la acción: el test debe probar aplicarDescuento
        assertThrows(RuntimeException.class, () -> 
            carrito.aplicarDescuento(descuentoExcedido)
        );
    }

    @Test
    @DisplayName("Debe bloquear modificaciones al iniciar checkout")
    void deberiaBloquearseAlHacerCheckout() {
        Carrito carrito = new Carrito(ClienteId.de(UUID.randomUUID()));
        ProductoId productoNuevo = new ProductoId(UUID.randomUUID());
        Money precio = Money.pesos(50.0);
        
        carrito.agregarProducto(new ProductoId(UUID.randomUUID()), 1, Money.pesos(100.0));
        carrito.iniciarCheckout();

        assertEquals(EstadoCarrito.EN_CHECKOUT, carrito.getEstado());

        assertThrows(RuntimeException.class, () -> 
            carrito.agregarProducto(productoNuevo, 1, precio)
        );
    }

    @Test
    @DisplayName("No debe permitir checkout si el total es <= $50 (RN-VEN-12)")
    void noDeberiaPermitirCheckoutSiTotalEsMenorOIgualA50() {
        Carrito carrito = new Carrito(ClienteId.de(UUID.randomUUID()));
        carrito.agregarProducto(new ProductoId(UUID.randomUUID()), 1, Money.pesos(40.0));

        Exception exception = assertThrows(DomainException.class, carrito::iniciarCheckout);
        
        assertTrue(exception.getMessage().toLowerCase().contains("monto minimo"));
    }

    @Test
    @DisplayName("Solo debe permitir abandonar si el estado es EN_CHECKOUT (RN-VEN-14)")
    void noDeberiaPermitirAbandonarSiNoEstaEnCheckout() {
        Carrito carrito = new Carrito(ClienteId.de(UUID.randomUUID()));
        
        assertThrows(DomainException.class, carrito::abandonar);
    }

    @Test
    @DisplayName("Flujo exitoso de abandono de carrito")
    void deberiaPermitirAbandonarSiEstaEnCheckout() {
        Carrito carrito = new Carrito(ClienteId.de(UUID.randomUUID()));
        carrito.agregarProducto(new ProductoId(UUID.randomUUID()), 1, Money.pesos(100.0));
        
        carrito.iniciarCheckout(); 
        carrito.abandonar();      

        assertEquals(EstadoCarrito.ABANDONADO, carrito.getEstado());
    }

    @Test
    @DisplayName("No debe permitir un descuento mayor al 30% del subtotal (RN-VEN-16)")
    void noDeberiaPermitirDescuentoMayorAl30PorCiento() {
        Carrito carrito = new Carrito(ClienteId.de(UUID.randomUUID()));
        carrito.agregarProducto(new ProductoId(UUID.randomUUID()), 1, Money.pesos(100.0)); 

        Money descuentoInvalido = Money.pesos(31.0);

        Exception exception = assertThrows(RuntimeException.class, () -> 
            carrito.aplicarDescuento(descuentoInvalido)
        );

        assertEquals("El descuento no puede ser mayor al 30% del subtotal", exception.getMessage());
    }
}