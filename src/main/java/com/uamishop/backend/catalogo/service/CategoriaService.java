/**
 * @file CategoriaService.java
 * @brief Servicio de aplicación para gestionar el agregado Categoria.
 *
 * Esta clase pertenece a la capa Application (Service Layer).
 *
 * Responsabilidades:
 * - Orquestar casos de uso relacionados con Categoría.
 * - Coordinar repositorios.
 * - Convertir entre DTOs y objetos de dominio.
 * - Manejar excepciones HTTP.
 *
 * Importante:
 * NO contiene lógica de persistencia.
 * NO contiene lógica de infraestructura.
 * La lógica de negocio pertenece al dominio.
 */

package com.uamishop.backend.catalogo.service;

import com.uamishop.backend.catalogo.repository.CategoriaRepository;
import com.uamishop.backend.catalogo.domain.Categoria;
import com.uamishop.backend.catalogo.domain.CategoriaId;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.uamishop.backend.catalogo.controller.dto.CategoriaRequest;
import com.uamishop.backend.catalogo.controller.dto.CategoriaResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @class CategoriaService
 *
 * Servicio de aplicación que implementa los casos de uso
 * del agregado Categoría.
 *
 * Está anotado con @Service para que Spring lo gestione
 * como componente.
 */
@Service
public class CategoriaService {

    /**
     * Repositorio de dominio.
     *
     * Se inyecta la interfaz, no la implementación concreta,
     * respetando el principio de inversión de dependencias.
     */
    private final CategoriaRepository categoriaRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param categoriaRepository contrato del repositorio
     */
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // =====================================================
    // CREAR CATEGORÍA
    // =====================================================

    /**
     * Crea una nueva categoría.
     *
     * Flujo:
     * 1. Genera un nuevo CategoriaId.
     * 2. Crea el agregado de dominio.
     * 3. Lo guarda en el repositorio.
     * 4. Devuelve un DTO de respuesta.
     *
     * @param request datos enviados desde el controlador
     * @return CategoriaResponse
     */
    public CategoriaResponse crear(CategoriaRequest request) {

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

    /**
     * Obtiene una categoría por su identificador.
     *
     * Si no existe, lanza excepción HTTP 404.
     *
     * @param id identificador UUID
     * @return CategoriaResponse
     */
    public CategoriaResponse obtenerPorId(UUID id) {

        Categoria categoria = categoriaRepository.findById(
                new CategoriaId(id)
        ).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Categoría no encontrada"
                )
        );

        return toResponse(categoria);
    }

    // =====================================================
    // ACTUALIZAR
    // =====================================================

    /**
     * Actualiza una categoría existente.
     *
     * Flujo:
     * 1. Busca la categoría.
     * 2. Aplica cambios usando el método del dominio.
     * 3. Guarda los cambios.
     * 4. Devuelve DTO actualizado.
     *
     * @param id identificador de la categoría
     * @param request datos actualizados
     * @return CategoriaResponse
     */
    public CategoriaResponse actualizar(UUID id, CategoriaRequest request) {

        Categoria categoria = categoriaRepository.findById(
                new CategoriaId(id)
        ).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        categoria.actualizar(
                request.nombre(),
                request.descripcion()
        );

        categoriaRepository.save(categoria);

        return toResponse(categoria);
    }

    // =====================================================
    // LISTAR TODAS
    // =====================================================

    /**
     * Obtiene todas las categorías registradas.
     *
     * @return lista de CategoriaResponse
     */
    public List<CategoriaResponse> listar() {

        return categoriaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // =====================================================
    // ELIMINAR
    // =====================================================

    /**
     * Elimina una categoría por su identificador.
     *
     * @param id identificador UUID
     */
    public void eliminar(UUID id) {
        categoriaRepository.deleteById(new CategoriaId(id));
    }

    // =====================================================
    // MAPPER DOMAIN → RESPONSE DTO
    // =====================================================

    /**
     * Convierte un agregado Categoria en un DTO de respuesta.
     *
     * @param categoria objeto de dominio
     * @return CategoriaResponse
     */
    private CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId().valor(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );
    }
}

