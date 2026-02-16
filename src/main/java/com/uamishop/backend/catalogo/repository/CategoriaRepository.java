package com.uamishop.backend.catalogo.repository;

import com.uamishop.backend.catalogo.domain.Categoria;
import com.uamishop.backend.catalogo.domain.CategoriaId;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository {

    Categoria save(Categoria categoria);

    Optional<Categoria> findById(CategoriaId id);

    List<Categoria> findAll();

    void deleteById(CategoriaId id);

    boolean existsById(CategoriaId id);

}
