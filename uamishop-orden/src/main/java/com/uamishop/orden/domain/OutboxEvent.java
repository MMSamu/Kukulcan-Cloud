package com.uamishop.orden.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvent {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String exchange;

    @Column(nullable = false)
    private String routingKey;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEnvio;
    private int reintentos;
    private String ultimoError;

    public OutboxEvent(UUID id, String exchange, String routingKey, String payload) {
        this.id = id;
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.payload = payload;
        this.status = OutboxStatus.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
        this.reintentos = 0;
    }

    public void marcarEnviado() {
        this.status = OutboxStatus.ENVIADO;
        this.fechaEnvio = LocalDateTime.now();
    }

    public void marcarFallido(String error) {
        this.status = OutboxStatus.FALLIDO;
        this.ultimoError = error;
        this.reintentos++;
    }
}
