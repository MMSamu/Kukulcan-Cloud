package com.uamishop.orden.publisher;

import com.uamishop.orden.domain.OutboxEvent;
import com.uamishop.orden.domain.OutboxStatus;
import com.uamishop.orden.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {

    private final OutboxEventRepository repository;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {
        List<OutboxEvent> eventosParaProcesar = repository
                .findByStatusIn(List.of(OutboxStatus.PENDIENTE, OutboxStatus.FALLIDO));

        if (eventosParaProcesar.isEmpty()) {
            return;
        }

        log.info("Outbox: Procesando {} eventos pendientes...", eventosParaProcesar.size());

        for (OutboxEvent event : eventosParaProcesar) {
            try {
                // Paso 1: Enviar a Rabbit
                rabbitTemplate.convertAndSend(event.getExchange(), event.getRoutingKey(), event.getPayload());

                // Paso 2 Si tuvo exito, marcamos como enviado
                event.marcarEnviado();
                log.info("Evento {} enviado correctamente.", event.getId());

                // Paso 3: Si fallo, guardamso el error
            } catch (Exception e) {
                log.error("Error al enviar el evento {}: {}", event.getId(), e.getMessage());
                event.marcarFallido(e.getMessage());
            }
            // Guardamos cada evento individualmente
            repository.save(event);
        }
        log.info("Outbox: Ciclo de envío completado.");
    }
}
