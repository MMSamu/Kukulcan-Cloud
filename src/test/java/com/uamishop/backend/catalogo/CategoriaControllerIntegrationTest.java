package com.uamishop.backend.catalogo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CategoriaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Debe crear una categoría correctamente (201)")
    void debeCrearCategoria() throws Exception {

        String json = """
        {
            "nombre": "Electrónica",
            "descripcion": "Categoría de prueba"
        }
        """;

        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Debe listar categorías (200)")
    void debeListarCategorias() throws Exception {

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debe obtener categoría por ID (200)")
    void debeObtenerCategoriaPorId() throws Exception {

        String json = """
        {
            "nombre": "Hogar",
            "descripcion": "Categoría hogar"
        }
        """;

        String response = mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extraemos el id del JSON manualmente
        String id = response.split("\"id\":\"")[1].split("\"")[0];

        mockMvc.perform(get("/api/categorias/" + id))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debe actualizar categoría (200)")
    void debeActualizarCategoria() throws Exception {

        String json = """
        {
            "nombre": "Ropa",
            "descripcion": "Categoría ropa"
        }
        """;

        String response = mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = response.split("\"id\":\"")[1].split("\"")[0];

        String updateJson = """
        {
            "nombre": "Ropa Actualizada",
            "descripcion": "Nueva descripción"
        }
        """;

        mockMvc.perform(put("/api/categorias/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debe eliminar categoría (204)")
    void debeEliminarCategoria() throws Exception {

        String json = """
        {
            "nombre": "Temporal",
            "descripcion": "Para eliminar"
        }
        """;

        String response = mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = response.split("\"id\":\"")[1].split("\"")[0];

        mockMvc.perform(delete("/api/categorias/" + id))
                .andExpect(status().isNoContent());
    }
}
