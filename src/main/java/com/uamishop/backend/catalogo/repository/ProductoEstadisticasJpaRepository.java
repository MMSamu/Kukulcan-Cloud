package com.uamishop.backend.catalogo.repository;

import com.uamishop.backend.catalogo.domain.ProductoEstadisticas;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductoEstadisticasJpaRepository extends JpaRepository<ProductoEstadisticas, UUID> {

    //  Que hace JpaRepository<ProductoEstadisticas, UUID, Spring crea automaticamente metodos com
    // save(), findById(), findAll() y delete() para la tabla PRODUTO_ESTADITICAS
    default List<ProductoEstadisticas> findMasVendidos(int limit) {
        return findAll(Sort.by(Sort.Direction.DESC, "cantidadVendida"))
                .stream()
                .limit(limit)
                .toList();
    }

}

