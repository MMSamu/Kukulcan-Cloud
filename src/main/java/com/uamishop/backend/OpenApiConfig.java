package com.uamishop.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * @brief Configuración global para Swagger.
 * @note Este archivo genera la interfaz gráfica en /swagger-ui/index.html
 * para que el profe pueda probar nuestra API sin usar Postman.
 */

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API REST - UamiShop ")
                        .version("1.0")
                        .description("API para la gestión del producots, categorias, carrito de compras y ventas.")
                        .contact(new Contact()
                                .name("Equipo Uamishop")
                                .email("contacto@uamishop.com"))
                        .license(new License()
                                .name("API License")
                                .url("http://uamishop.com/license")));


    }
}