package com.uamishop.orden.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uamishop.orden.domain.OutboxEvent;
import com.uamishop.orden.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    public void guardarEvento(String exchange, String routingKey, Object payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            OutboxEvent event = new OutboxEvent(UUID.randomUUID(), exchange, routingKey, jsonPayload);
            repository.save(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al serializar el evento a JSON", e);
        }
    }
}
