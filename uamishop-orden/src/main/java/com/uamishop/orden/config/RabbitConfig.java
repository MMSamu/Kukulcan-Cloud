package com.uamishop.orden.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // Exchanges
    public static final String EVENTS_EXCHANGE = "uamishop.events";

    // Nombres de Colas
    public static final String QUEUE_CATALOGO_PRODUCTO_COMPRADO = "catalogo.producto-comprado";
    public static final String QUEUE_CATALOGO_PRODUCTO_AGREGADO = "catalogo.producto-agregado-carrito";
    public static final String QUEUE_CARRITO_FINALIZADO = "uamishop.ventas.carrito.finalizado";

    // Routing Keys
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

    /* --- CONFIGURACIÓN DE BINDINGS --- */

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

    /* --- INFRAESTRUCTURA RABBIT --- */

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        // Esto fuerza la declaración de colas y exchanges al iniciar la app
        admin.setAutoStartup(true); 
        return admin;
    }
}