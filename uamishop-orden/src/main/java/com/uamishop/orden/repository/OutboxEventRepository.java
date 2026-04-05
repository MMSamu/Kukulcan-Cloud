package com.uamishop.orden.repository;

import com.uamishop.orden.domain.OutboxEvent;
import com.uamishop.orden.domain.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findByStatusIn(List<OutboxStatus> status);
}
