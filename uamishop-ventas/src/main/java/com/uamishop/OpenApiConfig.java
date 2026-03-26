package com.uamishop;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ventasOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API REST - UamiShop Ventas")
                        .version("1.0")
                        .description("API para la gestión de carrito y operaciones de ventas.")
                        .contact(new Contact().name("Equipo UamiShop")));
    }
}