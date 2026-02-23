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
 * - Las reglas de negocio se validan aquí y en el dominio.
 */

package com.uamishop.backend.catalogo.service;

import com.uamishop.backend.catalogo.repository.ProductoRepository;
import com.uamishop.backend.catalogo.repository.CategoriaRepository;
import com.uamishop.backend.catalogo.domain.*;
import com.uamishop.backend.shared.domain.Money;
import com.uamishop.backend.catalogo.controller.dto.*;
import com.uamishop.backend.catalogo.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

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

    public ProductoResponse crear(ProductoRequest request) {

        // ✅ Validación de regla de negocio: precio obligatorio y mayor a 0
        if (request.precio() == null || request.precio().doubleValue() <= 0) {
            throw new BusinessRuleException(
                    "PRECIO_INVALIDO",
                    "El precio del producto debe ser mayor a cero"
            );
        }

        // ✅ Validar que la categoría exista
        Categoria categoria = categoriaRepository.findById(
                new CategoriaId(request.categoriaId())
        ).orElseThrow(() -> new BusinessRuleException(
                "CATEGORIA_NO_ENCONTRADA",
                "La categoría especificada no existe"
        ));

        // Crear producto desde el dominio
        Producto producto = Producto.crear(
                request.nombre(),
                request.descripcion(),
                Money.pesos(request.precio().doubleValue()),
                categoria.getId()
        );

        productoRepository.save(producto);

        return toResponse(producto);
    }

    // =====================================================
    // ACTUALIZAR PRODUCTO
    // =====================================================

    public ProductoResponse actualizar(UUID id, ProductoRequest request) {

        Producto producto = productoRepository.findById(
                new ProductoId(id)
        ).orElseThrow(() -> new BusinessRuleException(
                "PRODUCTO_NO_ENCONTRADO",
                "El producto no existe"
        ));

        producto.actualizarNombreYDescripcion(
                request.nombre(),
                request.descripcion()
        );

        if (request.precio() != null) {

            if (request.precio().doubleValue() <= 0) {
                throw new BusinessRuleException(
                        "PRECIO_INVALIDO",
                        "El precio del producto debe ser mayor a cero"
                );
            }

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

    public void activar(UUID id) {

        Producto producto = productoRepository.findById(
                new ProductoId(id)
        ).orElseThrow(() -> new BusinessRuleException(
                "PRODUCTO_NO_ENCONTRADO",
                "El producto no existe"
        ));

        producto.activar();
        productoRepository.save(producto);
    }

    // =====================================================
    // DESACTIVAR PRODUCTO
    // =====================================================

    public void desactivar(UUID id) {

        Producto producto = productoRepository.findById(
                new ProductoId(id)
        ).orElseThrow(() -> new BusinessRuleException(
                "PRODUCTO_NO_ENCONTRADO",
                "El producto no existe"
        ));

        producto.desactivar();
        productoRepository.save(producto);
    }

    // =====================================================
    // OBTENER POR ID
    // =====================================================

    public ProductoResponse obtenerPorId(UUID id) {

        Producto producto = productoRepository.findById(
                new ProductoId(id)
        ).orElseThrow(() -> new BusinessRuleException(
                "PRODUCTO_NO_ENCONTRADO",
                "El producto no existe"
        ));

        return toResponse(producto);
    }

    // =====================================================
    // LISTAR TODOS
    // =====================================================

    public List<ProductoResponse> listar() {

        return productoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // =====================================================
    // MAPPER DOMAIN → DTO
    // =====================================================

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

