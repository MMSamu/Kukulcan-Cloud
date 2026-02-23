/**
 * @file ProductoService.java
 * @brief Servicio de aplicación que gestiona los casos de uso del agregado Producto.
 *
 * Esta clase pertenece a la capa Application (Service Layer).
 *
 * Responsabilidades:
 * - Orquestar casos de uso relacionados con Producto.
 * - Validar reglas de aplicación (como existencia de categoría).
 * - Coordinar repositorios.
 * - Convertir entre DTOs y objetos de dominio.
 *
 * Importante:
 * - No contiene lógica de persistencia.
 * - No contiene detalles de infraestructura.
 * - Las reglas de negocio están en el dominio (Producto).
 */

package com.uamishop.backend.catalogo.service;

import com.uamishop.backend.catalogo.repository.ProductoRepository;
import com.uamishop.backend.catalogo.repository.CategoriaRepository;
import com.uamishop.backend.catalogo.domain.*;
import com.uamishop.backend.shared.domain.Money;
import com.uamishop.backend.catalogo.controller.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    /**
     * Repositorio de dominio para Producto.
     */
    private final ProductoRepository productoRepository;

    /**
     * Repositorio de dominio para Categoría.
     * Se usa para validar existencia de categoría.
     */
    private final CategoriaRepository categoriaRepository;

    /**
     * Constructor con inyección de dependencias.
     */
    public ProductoService(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository
    ) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    // =====================================================
    // CREAR PRODUCTO
    // =====================================================

    /**
     * Crea un nuevo producto.
     *
     * Flujo:
     * 1. Verifica que la categoría exista.
     * 2. Crea el producto usando la fábrica del dominio.
     * 3. Guarda el producto.
     * 4. Devuelve DTO de respuesta.
     *
     * @param request datos enviados desde el controlador
     * @return ProductoResponse
     */
    public ProductoResponse crear(ProductoRequest request) {

        // 1️⃣ Validar que la categoría exista
        Categoria categoria = categoriaRepository.findById(
                new CategoriaId(request.categoriaId())
        ).orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        // 2️⃣ Crear el producto usando fábrica del dominio
        Producto producto = Producto.crear(
                request.nombre(),
                request.descripcion(),
                Money.pesos(request.precio().doubleValue()),
                categoria.getId()
        );

        // 3️⃣ Guardar
        productoRepository.save(producto);

        // 4️⃣ Retornar response
        return toResponse(producto);
    }

    // =====================================================
    // ACTUALIZAR PRODUCTO
    // =====================================================

    /**
     * Actualiza nombre, descripción y opcionalmente precio.
     *
     * @param id identificador del producto
     * @param request datos nuevos
     * @return ProductoResponse actualizado
     */
    public ProductoResponse actualizar(UUID id, ProductoRequest request) {

        Producto producto = productoRepository.findById(
                new ProductoId(id)
        ).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Actualizar datos básicos
        producto.actualizarNombreYDescripcion(
                request.nombre(),
                request.descripcion()
        );

        // Cambiar precio si fue enviado
        if (request.precio() != null) {
            producto.cambiarPrecio(
                    Money.pesos(request.precio().doubleValue())
            );
        }

        productoRepository.save(producto);

        return toResponse(producto);
    }

    // =====================================================
    // ACTIVAR PRODUCTO
    // =====================================================

    /**
     * Activa un producto (lo deja disponible).
     */
    public void activar(UUID id) {

        Producto producto = productoRepository.findById(
                new ProductoId(id)
        ).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.activar();
        productoRepository.save(producto);
    }

    // =====================================================
    // DESACTIVAR PRODUCTO
    // =====================================================

    /**
     * Desactiva un producto (lo deja no disponible).
     */
    public void desactivar(UUID id) {

        Producto producto = productoRepository.findById(
                new ProductoId(id)
        ).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.desactivar();
        productoRepository.save(producto);
    }

    // =====================================================
    // OBTENER POR ID
    // =====================================================

    /**
     * Obtiene un producto por su identificador.
     *
     * @param id UUID del producto
     * @return ProductoResponse
     */
    public ProductoResponse obtenerPorId(UUID id) {

        Producto producto = productoRepository.findById(
                new ProductoId(id)
        ).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return toResponse(producto);
    }

    // =====================================================
    // LISTAR TODOS
    // =====================================================

    /**
     * Lista todos los productos.
     *
     * @return lista de ProductoResponse
     */
    public List<ProductoResponse> listar() {

        return productoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // =====================================================
    // MAPPER DOMAIN → DTO
    // =====================================================

    /**
     * Convierte un agregado Producto en un DTO de respuesta.
     */
    private ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId().valor(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio().getCantidad(),
                producto.getCategoriaId().valor(),
                producto.isDisponible()
        );
    }
}

