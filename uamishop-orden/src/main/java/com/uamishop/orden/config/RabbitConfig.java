package com.uamishop.orden.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RabbitConfig {

    // --- EXCHANGES ---
    public static final String EVENTS_EXCHANGE = "uamishop.events";

    // --- NOMBRES DE COLAS (Sincronizadas con el Listener) ---
    public static final String CARRITO_FINALIZADO_QUEUE = "uamishop.ventas.carrito.finalizado";
    public static final String QUEUE_CATALOGO_PRODUCTO_COMPRADO = "catalogo.producto-comprado";
    public static final String QUEUE_CATALOGO_PRODUCTO_AGREGADO = "catalogo.producto-agregado-carrito";

    // --- ROUTING KEYS ---
    public static final String RK_CARRITO_FINALIZADO = "carrito.finalizado";
    public static final String RK_ORDEN_CREADA = "orden.creada";
    public static final String RK_PRODUCTO_COMPRADO = "producto.comprado";
    public static final String RK_PRODUCTO_AGREGADO = "producto.agregado-carrito";

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EVENTS_EXCHANGE);
    }

    /* --- DECLARACIÓN DE COLAS --- */

    @Bean
    public Queue queueCarritoFinalizado() {
        return new Queue(CARRITO_FINALIZADO_QUEUE, true);
    }

    @Bean
    public Queue catalogoProductoCompradoQueue() {
        return new Queue(QUEUE_CATALOGO_PRODUCTO_COMPRADO, true);
    }

    @Bean
    public Queue catalogoProductoAgregadoQueue() {
        return new Queue(QUEUE_CATALOGO_PRODUCTO_AGREGADO, true);
    }

    /* --- BINDINGS --- */

    @Bean
    public Binding bindingCarritoFinalizado(Queue queueCarritoFinalizado, TopicExchange eventsExchange) {
        return BindingBuilder.bind(queueCarritoFinalizado)
                .to(eventsExchange)
                .with(RK_CARRITO_FINALIZADO);
    }

    @Bean
    public Binding catalogoProductoCompradoBinding(Queue catalogoProductoCompradoQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(catalogoProductoCompradoQueue)
                .to(eventsExchange)
                .with(RK_PRODUCTO_COMPRADO);
    }

    @Bean
    public Binding catalogoProductoAgregadoBinding(Queue catalogoProductoAgregadoQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(catalogoProductoAgregadoQueue)
                .to(eventsExchange)
                .with(RK_PRODUCTO_AGREGADO);
    }

    /* --- INFRAESTRUCTURA --- */

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true); 
        return admin;
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}