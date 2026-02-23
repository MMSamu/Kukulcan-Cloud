package com.uamishop.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * @class OpenApiConfig
 *
 * @brief Configuración global de documentación OpenAPI (Swagger).
 *
 * @details
 * Esta clase configura la documentación automática de la API REST
 * utilizando la especificación OpenAPI 3.
 *
 * Gracias a esta configuración:
 * ✔ Se genera documentación automática de todos los endpoints.
 * ✔ Se habilita la interfaz gráfica Swagger UI.
 * ✔ Se facilita la prueba de la API sin herramientas externas como Postman.
 *
 * Swagger UI estará disponible en:
 * http://localhost:8080/swagger-ui/index.html
 *
 * Arquitectónicamente:
 * - Esta clase pertenece a la capa de configuración (Infrastructure).
 * - No contiene lógica de negocio.
 * - Solo define metadata de la API.
 */
@Configuration
public class OpenApiConfig {

    /**
     * @brief Define el Bean principal de configuración OpenAPI.
     *
     * @details
     * El método crea y configura un objeto OpenAPI que contiene
     * la información general de la API:
     *
     * - Título
     * - Versión
     * - Descripción
     * - Información de contacto
     * - Licencia
     *
     * Spring detecta este método gracias a @Bean
     * y lo registra automáticamente en el contexto.
     *
     * @return instancia configurada de OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("API REST - UamiShop")
                                .version("1.0")
                                .description(
                                        "API para la gestión de productos, " +
                                                "categorías, carrito de compras y ventas."
                                )
                                .contact(
                                        new Contact()
                                                .name("Equipo UamiShop")
                                                .email("contacto@uamishop.com")
                                )
                                .license(
                                        new License()
                                                .name("API License")
                                                .url("http://uamishop.com/license")
                                )
                );
    }
}