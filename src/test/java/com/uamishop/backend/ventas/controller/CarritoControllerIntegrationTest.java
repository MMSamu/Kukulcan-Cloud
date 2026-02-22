package com.uamishop.backend.ventas.controller;

import com.uamishop.backend.ventas.controller.dto.AgregarProductoRequest;
import com.uamishop.backend.ventas.controller.dto.CarritoRequest;
import com.uamishop.backend.ventas.controller.dto.CarritoResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * * @brief Pruebas de integración para CarritoController.
 * * @note Lau, aquí simulamos que somos el Frontend haciendo peticiones reales
 * a nuestra API para ver que todo se conecte bien (Paso 4 de la práctica).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CarritoControllerIntegrationTest {

    private static final String BASE_URL = "/api/carritos";

    @Autowired
    private TestRestTemplate restTemplate;

    @Nested
    @DisplayName("POST /api/carritos")
    class CrearCarrito {

        @Test
        @DisplayName("Crea un carrito vacío y retorna 201 Created")
        void crear_retorna201() {

            // 1. Preparamos los datos de la petición (El Request)

            UUID clienteIdFalso = UUID.randomUUID();
            CarritoRequest body = new CarritoRequest(clienteIdFalso);
            HttpEntity<CarritoRequest> request = new HttpEntity<>(body);

            // 2. Ejecutamos la petición POST al endpoint

            ResponseEntity<CarritoResponseDTO> response = restTemplate.exchange(
                    BASE_URL,
                    HttpMethod.POST,
                    request,
                    CarritoResponseDTO.class
            );

            // 3. Validamos que la respuesta sea correcta

            assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Debería regresar estatus 201");
            assertNotNull(response.getBody(), "El cuerpo de la respuesta no debe ser nulo");
            assertNotNull(response.getBody().id(), "El carrito creado debe tener un ID asignado");
        }
    }

    @Nested
    @DisplayName("POST /api/carritos/{id}/productos")
    class AgregarProducto {

        @Test
        @DisplayName("Retorna 400 Bad Request si la cantidad de productos es mayor a 10")
        void agregar_retorna400_cuandoCantidadEsExcesiva() {

            // 1. Preparamos un request con datos inválidos (15 unidades)

            UUID carritoId = UUID.randomUUID();
            UUID productoId = UUID.randomUUID();

            // Simulamos el request con 15 productos y un precio válido

            AgregarProductoRequest body = new AgregarProductoRequest(
                    productoId,
                    15, // ¡Aquí violamos la regla del @Max(10)!
                    new java.math.BigDecimal("100.00")
            );
            HttpEntity<AgregarProductoRequest> request = new HttpEntity<>(body);

            // 2. Ejecutamos la petición POST

            ResponseEntity<String> response = restTemplate.exchange(
                    BASE_URL + "/" + carritoId + "/productos",
                    HttpMethod.POST,
                    request,
                    String.class // Lo leemos como String para ver el JSON de error
            );

            // 3. Validamos que el servidor nos haya bateado con un 400

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Debería rechazar la petición con un 400");
            assertNotNull(response.getBody());

            // Comprobamos que el mensaje de error de nuestro DTO esté en la respuesta

            org.junit.jupiter.api.Assertions.assertTrue(
                    response.getBody().contains("Máximo 10 unidades permitidas"),
                    "El JSON de error debe contener nuestro mensaje de validación"
            );
        }
    }
}