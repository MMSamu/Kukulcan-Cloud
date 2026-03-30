package com.uamishop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate; // Importa esto

@SpringBootApplication
public class VentasApplication {

    public static void main(String[] args) {
        SpringApplication.run(VentasApplication.class, args);
    }

    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}