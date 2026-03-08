package com.uamishop.backend.catalogo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uamishop.backend.catalogo.domain.ProductoEstadisticas;
import com.uamishop.backend.catalogo.repository.ProductoEstadisticasJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Este test valida GET /api/v1/productos/mas-vendidos
//verifica el status 200
//que regrese una lista
//y que los valores sean los correctos

@SpringBootTest
@AutoConfigureMockMvc
class ProductoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoEstadisticasJpaRepository estadisticasRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID productoId;

    @BeforeEach
    void setup() {

        estadisticasRepository.deleteAll();

        productoId = UUID.randomUUID();

        ProductoEstadisticas stats = new ProductoEstadisticas(productoId);
        stats.setVentasTotales(10L);
        stats.setCantidadVendida(50L);
        stats.setVecesAgregadoAlCarrito(20L);
        stats.setUltimaVentaAt(Instant.now());

        estadisticasRepository.save(stats);
    }

    // =====================================================
    // TEST: GET /api/v1/productos/mas-vendidos
    // =====================================================

    @Test
    void deberiaObtenerProductosMasVendidos() throws Exception {

        mockMvc.perform(get("/api/v1/productos/mas-vendidos")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ventasTotales").value(10))
                .andExpect(jsonPath("$[0].cantidadVendida").value(50))
                .andExpect(jsonPath("$[0].vecesAgregadoAlCarrito").value(20));
    }

    // =====================================================
    // TEST: GET /api/v1/productos/{id}/estadisticas
    // =====================================================

    //Este test verifica el status 200
    // y JSON correcto
    @Test
    void deberiaObtenerEstadisticasDeProducto() throws Exception {

        mockMvc.perform(get("/api/v1/productos/" + productoId + "/estadisticas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ventasTotales").value(10))
                .andExpect(jsonPath("$.cantidadVendida").value(50))
                .andExpect(jsonPath("$.vecesAgregadoAlCarrito").value(20));
    }

    // =====================================================
    // TEST: producto no existe
    // =====================================================

    //verifica el status 404
    @Test
    void deberiaRetornar404SiProductoNoExiste() throws Exception {

        UUID idInexistente = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/productos/" + idInexistente + "/estadisticas"))
                .andExpect(status().isNotFound());
    }
}
