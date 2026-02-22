package com.uamishop.backend.catalogo.service;

import com.uamishop.backend.catalogo.repository.ProductoRepository;
import com.uamishop.backend.catalogo.repository.CategoriaRepository;
import com.uamishop.backend.catalogo.domain.*;
import com.uamishop.backend.shared.domain.Money;
import com.uamishop.backend.catalogo.controller.dto.*;
import org.springframework.stereotype.Service;
import com.uamishop.backend.catalogo.repository.*;

import java.math.BigDecimal;
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

    // =============================
    // CREAR
   // =============================
    public ProductoResponse crear(ProductoRequest request) {

        // 1️⃣ Validar que la categoría exista
        Categoria categoria = categoriaRepository.findById(
                new CategoriaId(request.categoriaId())
        ).orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        // 2️⃣ Crear el producto usando la fábrica del dominio
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

    // =============================
    // ACTUALIZAR
    // =============================
    public ProductoResponse actualizar(UUID id, ProductoRequest request) {

        Producto producto = productoRepository.findById(
                new ProductoId(id)
        ).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.actualizarNombreYDescripcion(
                request.nombre(),
                request.descripcion()
        );

        if (request.precio() != null) {
            producto.cambiarPrecio(
                    Money.pesos(request.precio().doubleValue())
            );
        }

        productoRepository.save(producto);

        return toResponse(producto);
    }

    // =============================
    // ACTIVAR
    // =============================
    public void activar(UUID id) {

        Producto producto = productoRepository.findById(
                new ProductoId(id)
        ).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.activar();
        productoRepository.save(producto);
    }

    // =============================
// OBTENER POR ID
// =============================
    public ProductoResponse obtenerPorId(UUID id) {

        Producto producto = productoRepository.findById(
                new ProductoId(id)
        ).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return toResponse(producto);
    }

    // =============================
// LISTAR TODOS
// =============================
    public List<ProductoResponse> listar() {

        return productoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // =============================
    // DESACTIVAR
    // =============================
    public void desactivar(UUID id) {

        Producto producto = productoRepository.findById(
                new ProductoId(id)
        ).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.desactivar();
        productoRepository.save(producto);
    }

    // =============================
    // MAPPER
    // =============================
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

