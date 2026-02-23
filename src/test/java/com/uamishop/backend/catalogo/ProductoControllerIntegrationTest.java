package com.uamishop.backend.catalogo;

import com.uamishop.backend.catalogo.domain.Categoria;
import com.uamishop.backend.catalogo.domain.CategoriaId;
import com.uamishop.backend.catalogo.repository.CategoriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Categoria crearCategoriaDePrueba() {
        Categoria categoria = new Categoria(
                new CategoriaId(UUID.randomUUID()),
                "Electrónica",
                "Categoría de prueba"
        );
        return categoriaRepository.save(categoria);
    }

    @Test
    @DisplayName("Debe crear un producto correctamente (201)")
    void debeCrearProducto() throws Exception {

        Categoria categoria = crearCategoriaDePrueba();

        String json = """
        {
            "nombre": "Producto Test",
            "descripcion": "Descripción Test",
            "precio": 1500.0,
            "stock": 10,
            "categoriaId": "%s"
        }
        """.formatted(categoria.getId().valor());

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }
}