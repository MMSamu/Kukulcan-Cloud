package com.uamishop.backend.catalogo.repository;

import com.uamishop.backend.catalogo.domain.Producto;
import com.uamishop.backend.catalogo.domain.ProductoId;
import com.uamishop.backend.catalogo.domain.CategoriaId;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository {

    Producto save(Producto producto);

    Optional<Producto> findById(ProductoId id);

    List<Producto> findAll();

    List<Producto> findByCategoriaId(CategoriaId categoriaId);

    void deleteById(ProductoId id);

    boolean existsById(ProductoId id);
}