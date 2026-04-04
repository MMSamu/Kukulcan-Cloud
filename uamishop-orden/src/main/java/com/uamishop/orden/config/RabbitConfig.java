package com.uamishop.orden.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // --- EXCHANGES ---
    public static final String EVENTS_EXCHANGE = "uamishop.events";

    // --- NOMBRES DE COLAS ---
    // Estas son las constantes que tu Listener usa en @RabbitListener(queues = ...)
    public static final String QUEUE_CATALOGO_PRODUCTO_COMPRADO = "catalogo.producto-comprado";
    public static final String QUEUE_CATALOGO_PRODUCTO_AGREGADO = "catalogo.producto-agregado-carrito";
    public static final String QUEUE_CARRITO_FINALIZADO = "uamishop.ventas.carrito.finalizado";

    // --- ROUTING KEYS ---
    public static final String RK_PRODUCTO_COMPRADO = "producto.comprado";
    public static final String RK_PRODUCTO_AGREGADO = "producto.agregado-carrito";
    public static final String RK_ORDEN_CREADA = "orden.creada";
    public static final String RK_CARRITO_FINALIZADO = "carrito.finalizado";

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EVENTS_EXCHANGE);
    }

    /* --- DECLARACIÓN DE COLAS --- */

    @Bean
    public Queue catalogoProductoCompradoQueue() {
        return new Queue(QUEUE_CATALOGO_PRODUCTO_COMPRADO, true);
    }

    @Bean
    public Queue catalogoProductoAgregadoQueue() {
        return new Queue(QUEUE_CATALOGO_PRODUCTO_AGREGADO, true);
    }

    @Bean
    public Queue queueCarritoFinalizado() {
        return new Queue(QUEUE_CARRITO_FINALIZADO, true);
    }

    /* --- CONFIGURACIÓN DE BINDINGS (Unión de Cola + Exchange + Routing Key) --- */

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

    @Bean
    public Binding bindingCarritoFinalizado(Queue queueCarritoFinalizado, TopicExchange eventsExchange) {
        return BindingBuilder.bind(queueCarritoFinalizado)
                .to(eventsExchange)
                .with(RK_CARRITO_FINALIZADO);
    }

    /* --- INFRAESTRUCTURA Y SERIALIZACIÓN --- */

    /**
     * Este Bean es vital para que RabbitMQ pueda transformar los JSON 
     * que vienen de Ventas en objetos Java automáticamente.
     */
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
        // Esto asegura que al levantar la app, RabbitMQ cree las colas si no existen
        admin.setAutoStartup(true); 
        return admin;
    }
}