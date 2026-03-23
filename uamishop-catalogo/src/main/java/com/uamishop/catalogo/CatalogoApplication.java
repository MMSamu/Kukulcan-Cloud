package com.uamishop.catalogo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@SpringBootApplication
@EnableAsync
@EnableRabbit
public class CatalogoApplication {
    public static void main(String[] args) {
        SpringApplication.run(CatalogoApplication.class, args);
    }
}