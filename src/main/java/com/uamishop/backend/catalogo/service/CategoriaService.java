package com.uamishop.backend.catalogo.service;

import com.uamishop.backend.catalogo.repository.CategoriaRepository;
import com.uamishop.backend.catalogo.domain.Categoria;
import com.uamishop.backend.catalogo.domain.CategoriaId;
import org.springframework.stereotype.Service;
import com.uamishop.backend.catalogo.controller.dto.CategoriaRequest;
import com.uamishop.backend.catalogo.controller.dto.CategoriaResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // =============================
    // CREAR
    // =============================
    public CategoriaResponse crear(CategoriaRequest request) {

        Categoria categoria = new Categoria(
                CategoriaId.generar(),
                request.nombre(),
                request.descripcion()
        );

        categoriaRepository.save(categoria);

        return toResponse(categoria);
    }

    // =============================
    // OBTENER POR ID
    // =============================
    public CategoriaResponse obtenerPorId(UUID id) {

        Categoria categoria = categoriaRepository.findById(
                new CategoriaId(id)
        ).orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        return toResponse(categoria);
    }

    // =============================
    // ACTUALIZAR
    // =============================
    public CategoriaResponse actualizar(UUID id, CategoriaRequest request) {

        Categoria categoria = categoriaRepository.findById(
                new CategoriaId(id)
        ).orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        categoria.actualizar(
                request.nombre(),
                request.descripcion()
        );

        categoriaRepository.save(categoria);

        return toResponse(categoria);
    }

    // =============================
    // LISTAR
    // =============================
    public List<CategoriaResponse> listar() {

        return categoriaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // =============================
    // ELIMINAR
    // =============================
    public void eliminar(UUID id) {
        categoriaRepository.deleteById(new CategoriaId(id));
    }

    // =============================
    // MAPPER
    // =============================
    private CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId().valor(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );
    }
}

