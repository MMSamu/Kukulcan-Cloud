package com.uamishop.backend.ventas.domain;

import com.uamishop.backend.ventas.controller.CarritoController;

import com.uamishop.backend.shared.domain.Money;
import com.uamishop.backend.ventas.service.CarritoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarritoController.class)
class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarritoService service;

    @Test
    void deberiaCrearUnCarrito() throws Exception {
        UUID clienteId = UUID.randomUUID();
        Carrito carrito = new Carrito(clienteId);

        //Asocia un cliente a un nuevo carrito
        when(service.crear(any(UUID.class))).thenReturn(carrito);

        mockMvc.perform(post("/api/carritos")
                .param("clienteId", clienteId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(clienteId.toString()));
    }

    @Test
    void deberiaObtenerCarritoPorId() throws Exception {
        UUID id = UUID.randomUUID();
        Carrito carrito = new Carrito(UUID.randomUUID());

        //Busca por el ValueObject CarritoId
        when(service.obtenerCarrito(any(CarritoId.class))).thenReturn(carrito);

        mockMvc.perform(get("/api/carritos/{id}", id))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaAgregarProductoAlCarrito() throws Exception {
        UUID id = UUID.randomUUID();
        UUID productoId = UUID.randomUUID();
        String moneyJson = "{\"cantidad\": 150.0, \"moneda\": \"MXN\"}";

        //Simula añadir items al carrito con cantidad
        when(service.agregarProducto(any(CarritoId.class), eq(productoId), eq(2), any(Money.class)))
                .thenReturn(new Carrito(UUID.randomUUID()));

        mockMvc.perform(post("/api/carritos/{id}/productos", id)
                .param("productoId", productoId.toString())
                .param("cantidad", "2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(moneyJson))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaModificarCantidadDeProducto() throws Exception {
        UUID id = UUID.randomUUID();
        UUID pId = UUID.randomUUID();

        //Valida actualizacipn de la cantidad de un Item
        mockMvc.perform(patch("/api/carritos/{id}/productos/{pId}", id, pId)
                .param("cantidad", "5"))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaEliminarProductoDelCarrito() throws Exception {
        UUID id = UUID.randomUUID();
        UUID pId = UUID.randomUUID();

        //Verifica la eliminación de un producto específico del carrito
        mockMvc.perform(delete("/api/carritos/{id}/productos/{pId}", id, pId))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaVaciarElCarrito() throws Exception {
        UUID id = UUID.randomUUID();

        //Verifica la eliminación de todos los productos del carrito
        mockMvc.perform(delete("/api/carritos/{id}/productos", id))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaIniciarCheckout() throws Exception {
        UUID id = UUID.randomUUID();

        //Inicia la transición del carrito a estado de checkout
        mockMvc.perform(post("/api/carritos/{id}/checkout", id))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaCompletarCheckout() throws Exception {
        UUID id = UUID.randomUUID();

        //Finaliza el proceso de compra, marcando el carrito como completado
        mockMvc.perform(post("/api/carritos/{id}/completar", id))
                .andExpect(status().isOk());
    }

    @Test
    void deberiaAbandonarCarrito() throws Exception {
        UUID id = UUID.randomUUID();

        //VAlida la acción de abandonar el carrito, lo que podría marcarlo como inactivo o eliminarlo
        mockMvc.perform(post("/api/carritos/{id}/abandonar", id))
                .andExpect(status().isOk());
    }
}