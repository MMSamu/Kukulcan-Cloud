package com.uamishop.orden.repository;

import com.uamishop.orden.domain.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface OrdenJpaRepository extends JpaRepository<Orden, UUID> {

}
