package com.uamishop.catalogo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "uamishop.events";

    public static final String QUEUE_CATALOGO_PRODUCTO_AGREGADO = "catalogo.producto-agregado";
    public static final String QUEUE_CATALOGO_PRODUCTO_COMPRADO = "catalogo.producto-comprado";

    public static final String RK_PRODUCTO_AGREGADO = "producto.agregado";
    public static final String RK_PRODUCTO_COMPRADO = "producto.comprado";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue queueProductoAgregado() {
        return new Queue(QUEUE_CATALOGO_PRODUCTO_AGREGADO, true);
    }

    @Bean
    public Queue queueProductoComprado() {
        return new Queue(QUEUE_CATALOGO_PRODUCTO_COMPRADO, true);
    }

    @Bean
    public Binding bindingProductoAgregado() {
        return BindingBuilder
                .bind(queueProductoAgregado())
                .to(exchange())
                .with(RK_PRODUCTO_AGREGADO);
    }

    @Bean
    public Binding bindingProductoComprado() {
        return BindingBuilder
                .bind(queueProductoComprado())
                .to(exchange())
                .with(RK_PRODUCTO_COMPRADO);
    }
}