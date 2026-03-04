package com.uamishop.backend.orden.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uamishop.backend.orden.controller.dto.*;
import com.uamishop.backend.shared.domain.Money;
import com.uamishop.backend.ventas.domain.Carrito;
import com.uamishop.backend.ventas.repository.CarritoJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.uamishop.backend.shared.domain.ProductoId;
import com.uamishop.backend.shared.domain.ClienteId;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Se corrigio es test de orden, importando clases que no habian aqui y algunas eran privadas

@SpringBootTest
@AutoConfigureMockMvc
class OrdenControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CarritoJpaRepository carritoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Debe crear una orden desde el carrito correctamente (201)")
    void debeCrearOrdenDesdeCarrito() throws Exception {

        ClienteId clienteId = ClienteId.generar();
        Carrito carrito = new Carrito(clienteId);

        ProductoId productoId = ProductoId.generar();
        carrito.agregarProducto(productoId, 2, Money.pesos(100));

        carrito.iniciarCheckout();
        carritoRepository.save(carrito);

        DireccionEnvioRequest direccion = new DireccionEnvioRequest(
                "Calle Falsa 123",
                "10",
                "01234",
                "Ciudad de México",
                "CDMX",
                "5512345678");

        mockMvc.perform(post("/api/v2/ordenes/" + carrito.getId().getValor() + "/orden")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(direccion)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value(clienteId.getValor().toString()))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @DisplayName("Debe buscar una orden por ID (200)")
    void debeBuscarOrdenPorId() throws Exception {

        ClienteId clienteId = ClienteId.generar();
        Carrito carrito = new Carrito(clienteId);

        ProductoId productoId = ProductoId.generar();
        carrito.agregarProducto(productoId, 1, Money.pesos(60));

        carrito.iniciarCheckout();
        carritoRepository.save(carrito);

        DireccionEnvioRequest direccion = new DireccionEnvioRequest(
                "Calle 1", "1", "12345", "Ciudad", "Estado", "5512345678");

        String response = mockMvc.perform(post("/api/v2/ordenes/" + carrito.getId().getValor() + "/orden")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(direccion)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String ordenId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(get("/api/v2/ordenes/" + ordenId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ordenId));
    }

    @Test
    @DisplayName("Debe listar todas las ordenes (200)")
    void debeListarOrdenes() throws Exception {
        mockMvc.perform(get("/api/v2/ordenes"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debe procesar el flujo completo de una orden (Pendiente -> Confirmada -> Preparación)")
    void debeProcesarFlujoOrden() throws Exception {

        ClienteId clienteId = ClienteId.generar();
        Carrito carrito = new Carrito(clienteId);

        ProductoId productoId = ProductoId.generar();
        carrito.agregarProducto(productoId, 1, Money.pesos(75));

        carrito.iniciarCheckout();
        carritoRepository.save(carrito);

        DireccionEnvioRequest direccion = new DireccionEnvioRequest(
                "Calle 2", "2", "54321", "Ciudad", "Estado", "5587654321");

        String response = mockMvc.perform(post("/api/v2/ordenes/" + carrito.getId().getValor() + "/orden")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(direccion)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String ordenId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(post("/api/v2/ordenes/" + ordenId + "/confirmar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));

        PagoRequest pagoRequest = new PagoRequest("PAGO12345678");

        mockMvc.perform(post("/api/v2/ordenes/" + ordenId + "/procesar-pago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PREPARACION"));
    }

    @Test
    @DisplayName("Debe cancelar una orden (200)")
    void debeCancelarOrden() throws Exception {

        ClienteId clienteId = ClienteId.generar();
        Carrito carrito = new Carrito(clienteId);

        ProductoId productoId = ProductoId.generar();
        carrito.agregarProducto(productoId, 1, Money.pesos(100));

        carrito.iniciarCheckout();
        carritoRepository.save(carrito);

        DireccionEnvioRequest direccion = new DireccionEnvioRequest(
                "Calle 3", "3", "11223", "Ciudad", "Estado", "5512341234");

        String response = mockMvc.perform(post("/api/v2/ordenes/" + carrito.getId().getValor() + "/orden")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(direccion)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String ordenId = objectMapper.readTree(response).get("id").asText();

        CancelacionRequest cancelRequest =
                new CancelacionRequest("El cliente ya no desea el producto");

        mockMvc.perform(post("/api/v2/ordenes/" + ordenId + "/cancelar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }
}


