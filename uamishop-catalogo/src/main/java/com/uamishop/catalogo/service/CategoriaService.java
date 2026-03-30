/**
 * @file CategoriaService.java
 * @brief Servicio de aplicación para gestionar el agregado Categoria.
 */

package com.uamishop.catalogo.service;

import com.uamishop.catalogo.repository.CategoriaRepository;
import com.uamishop.catalogo.domain.Categoria;
import com.uamishop.catalogo.shared.domain.CategoriaId;
import com.uamishop.catalogo.controller.dto.CategoriaRequest;
import com.uamishop.catalogo.controller.dto.CategoriaResponse;
import com.uamishop.catalogo.exception.BusinessRuleException; // Usando tu excepción personalizada

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante para persistencia
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @class CategoriaService
 * Servicio de aplicación que implementa los casos de uso del agregado Categoría.
 */
@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // =====================================================
    // CREAR CATEGORÍA
    // =====================================================
    @Transactional
    public CategoriaResponse crear(@Valid CategoriaRequest request) {
        // Validar que el nombre no sea nulo o vacío antes de crear
        if (request.nombre() == null || request.nombre().isBlank()) {
            throw new BusinessRuleException("NOMBRE_INVALIDO", "El nombre de la categoría es obligatorio");
        }

        Categoria categoria = new Categoria(
                CategoriaId.generar(),
                request.nombre(),
                request.descripcion()
        );

        categoriaRepository.save(categoria);
        return toResponse(categoria);
    }

    // =====================================================
    // OBTENER POR ID
    // =====================================================
    @Transactional(readOnly = true)
    public CategoriaResponse obtenerPorId(UUID id) {
        Categoria categoria = categoriaRepository.findById(new CategoriaId(id))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Categoría no encontrada"));

        return toResponse(categoria);
    }

    // =====================================================
    // ACTUALIZAR
    // =====================================================
    /**
     * Se agrega @Transactional para asegurar que los cambios realizados
     * en el objeto de dominio se sincronicen con la base de datos.
     */
    @Transactional
    public CategoriaResponse actualizar(UUID id, CategoriaRequest request) {

        // 1. Buscar la categoría
        Categoria categoria = categoriaRepository.findById(new CategoriaId(id))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No se puede actualizar: Categoría no encontrada"));

        // 2. Aplicar cambios en el objeto de dominio
        // IMPORTANTE: Verifica que en Categoria.java el método actualizar() 
        // asigne: this.nombre = nombre; y this.descripcion = descripcion;
        categoria.actualizar(
                request.nombre(),
                request.descripcion()
        );

        // 3. Forzar el guardado y flush
        Categoria actualizada = categoriaRepository.save(categoria);

        return toResponse(actualizada);
    }

    // =====================================================
    // LISTAR TODAS
    // =====================================================
    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // =====================================================
    // ELIMINAR
    // =====================================================
    @Transactional
    public void eliminar(UUID id) {
        CategoriaId categoriaId = new CategoriaId(id);
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la categoría a eliminar");
        }
        categoriaRepository.deleteById(categoriaId);
    }

    // =====================================================
    // MAPPER DOMAIN → RESPONSE DTO
    // =====================================================
    private CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId().valor(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );
    }
}