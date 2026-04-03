package com.uamishop.ventas.config;

import java.util.HashMap;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EVENTS_EXCHANGE = "uamishop.events";
    
    public static final String QUEUE_CATALOGO_PRODUCTO_COMPRADO = "catalogo.producto-comprado";
    public static final String QUEUE_CATALOGO_PRODUCTO_AGREGADO = "catalogo.producto-agregado-carrito";
    public static final String RK_PRODUCTO_COMPRADO = "producto.comprado";
    public static final String RK_PRODUCTO_AGREGADO = "producto.agregado-carrito";
    public static final String RK_PRODUCTO_ACTIVADO = "producto.activado";

    public static final String RK_ORDEN_CREADA = "orden.creada";
    public static final String Q_ORDEN_CREADA_VENTAS = "ventas.orden-creada";

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EVENTS_EXCHANGE);
    }

    @Bean
    public Queue catalogoProductoCompradoQueue() {
        return new Queue(QUEUE_CATALOGO_PRODUCTO_COMPRADO, true);
    }

    @Bean
    public Queue catalogoProductoAgregadoQueue() {
        return new Queue(QUEUE_CATALOGO_PRODUCTO_AGREGADO, true);
    }

    @Bean
    public Binding catalogoProductoCompradoBinding(Queue catalogoProductoCompradoQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(catalogoProductoCompradoQueue).to(eventsExchange).with(RK_PRODUCTO_COMPRADO);
    }

    @Bean
    public Binding catalogoProductoAgregadoBinding(Queue catalogoProductoAgregadoQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(catalogoProductoAgregadoQueue).to(eventsExchange).with(RK_PRODUCTO_AGREGADO);
    }

    @Bean
    public Queue productQueue() {
        return new Queue("ventas.producto.activado", true);
    }

    @Bean
    public Binding productActivadoBinding(Queue productQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(productQueue)
                .to(eventsExchange)
                .with(RK_PRODUCTO_ACTIVADO);
    }

    @Bean
    public Queue ordenCreadaVentasQueue() {
        return new Queue(Q_ORDEN_CREADA_VENTAS, true);
    }

    @Bean
    public Binding ordenCreadaVentasBinding(Queue ordenCreadaVentasQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(ordenCreadaVentasQueue).to(eventsExchange).with(RK_ORDEN_CREADA);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

}