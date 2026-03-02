package com.uamishop.backend.ventas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uamishop.backend.shared.domain.ClienteId;
import com.uamishop.backend.shared.domain.Money;
import com.uamishop.backend.shared.domain.ProductoId;
import com.uamishop.backend.ventas.controller.dto.AgregarProductoRequest;
import com.uamishop.backend.ventas.controller.dto.CarritoRequest;
import com.uamishop.backend.ventas.domain.Carrito;
import com.uamishop.backend.ventas.domain.CarritoId;
import com.uamishop.backend.ventas.service.CarritoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarritoController.class) // Carga la capa web
class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc; // Cliente para simular peticiones HTTP

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos a JSON

    @MockBean
    private CarritoService carritoService; // Mockea el servicio para aislar el controlador

    @Test
    @DisplayName("Debe crear un carrito exitosamente (POST /api/v1/carritos)")
    void crearCarritoTest() throws Exception {
        // GIVEN: Preparamos los datos
        UUID clienteUuid = UUID.randomUUID();
        CarritoRequest request = new CarritoRequest(clienteUuid);
        Carrito carritoSimulado = new Carrito(ClienteId.de(clienteUuid));

        // Comportamiento del Mock
        when(carritoService.crear(any(ClienteId.class))).thenReturn(carritoSimulado);

        // WHEN & THEN: Ejecutam y valida
        mockMvc.perform(post("/api/v1/carritos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value(clienteUuid.toString()))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    @DisplayName("Debe obtener un carrito por ID (GET /api/v1/carritos/{id})")
    void obtenerCarritoTest() throws Exception {
        // GIVEN
        UUID carritoUuid = UUID.randomUUID();
        UUID clienteUuid = UUID.randomUUID();
        Carrito carritoSimulado = new Carrito(ClienteId.de(clienteUuid));

        when(carritoService.obtenerCarrito(any(CarritoId.class))).thenReturn(carritoSimulado);

        // WHEN & THEN
        mockMvc.perform(get("/api/v1/carritos/" + carritoUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(clienteUuid.toString()));
    }

    @Test
    @DisplayName("Debe agregar un producto al carrito (POST /api/v1/carritos/{id}/productos)")
    void agregarProductoTest() throws Exception {
        // GIVEN
        UUID carritoUuid = UUID.randomUUID();
        AgregarProductoRequest request = new AgregarProductoRequest(
                UUID.randomUUID(), 
                2, 
                new BigDecimal("150.00")
        );
        
        Carrito carritoActualizado = new Carrito(ClienteId.de(UUID.randomUUID()));
        // Agregamos manualmente al carrito simulado para que el JSON de respuesta tenga items
        carritoActualizado.agregarProducto(
            new ProductoId(request.productoId()), 
            request.cantidad(), 
            Money.pesos(request.precioMonto().doubleValue())
        );

        when(carritoService.agregarProducto(any(), any(), anyInt(), any())).thenReturn(carritoActualizado);

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/carritos/" + carritoUuid + "/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].cantidad").value(2));
    }
}