package com.uamishop.backend.ventas.api;

import com.uamishop.backend.shared.domain.ClienteId;
import com.uamishop.backend.shared.domain.Money;
import com.uamishop.backend.shared.domain.ProductoId;
import com.uamishop.backend.ventas.domain.Carrito;
import com.uamishop.backend.ventas.domain.CarritoId;
import com.uamishop.backend.ventas.service.CarritoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // Carga el contexto completo de Spring
@Transactional  // Revierte los cambios en la BD después de cada test
class VentasApiIntegrationTest {

    @Autowired
    private VentasApi ventasApi; // Probamos la interfaz pública

    @Autowired
    private CarritoService carritoService; // Para preparar datos previos

    //Debe obtener el resumen completo de un carrito existente
    @Test
    void debeObtenerResumenCorrectamente() {
        // GIVEN: Creamos un carrito y le agregamos un producto usando el servicio
        ClienteId clienteId = ClienteId.de(UUID.randomUUID());
        Carrito carritoPersistido = carritoService.crear(clienteId);
        CarritoId carritoId = carritoPersistido.getId();
        
        ProductoId productoId = new ProductoId(UUID.randomUUID());
        carritoService.agregarProducto(carritoId, productoId, 2, Money.pesos(100.0));

        // WHEN: Llamamos a la API interna
        CarritoResumen resumen = ventasApi.obtenerResumen(carritoId.value());

        // THEN: Validamos la integración con la BD
        assertNotNull(resumen);
        assertEquals(carritoId.value(), resumen.carritoId());
        assertEquals(clienteId, resumen.clienteId());
        assertEquals("ACTIVO", resumen.estado());
        assertEquals(1, resumen.items().size());
        assertEquals(productoId, resumen.items().get(0).productoId());
        assertEquals(2, resumen.items().get(0).cantidad());
    }

    @Test
    //Debe completar el checkout a través de la API pública
    void debeCompletarCheckoutPublico() {
        // GIVEN: Un carrito con total suficiente para hacer checkout
        Carrito carrito = carritoService.crear(ClienteId.de(UUID.randomUUID()));
        carritoService.agregarProducto(carrito.getId(), new ProductoId(UUID.randomUUID()), 1, Money.pesos(100.0));
        
        // Requiere estar en "EN_CHECKOUT" antes de completar
        carritoService.iniciarCheckout(carrito.getId());

        // WHEN: Llamamos al método público para completar el checkout
        ventasApi.completarCheckoutPublico(carrito.getId().value());

        // THEN: Validamos que el estado se actualizó a "COMPLETADO"
        CarritoResumen resumenFinal = ventasApi.obtenerResumen(carrito.getId().value());
        assertEquals("COMPLETADO", resumenFinal.estado());
    }
}