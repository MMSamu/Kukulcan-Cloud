package com.uamishop.backend.catalogo.service;

import com.uamishop.backend.catalogo.repository.ProductoRepository;
import com.uamishop.backend.catalogo.repository.CategoriaRepository;
import com.uamishop.backend.catalogo.domain.*;
import com.uamishop.backend.shared.domain.Money;

import java.util.List;

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

    public Producto crear(
            String nombre,
            String descripcion,
            Money precio,
            CategoriaId categoriaId
    ) {

        if (!categoriaRepository.existsById(categoriaId)) {
            throw new IllegalArgumentException("La categoría no existe");
        }

        Producto producto = Producto.crear(
                nombre,
                descripcion,
                precio,
                categoriaId
        );

        return productoRepository.save(producto);
    }

    public Producto cambiarPrecio(ProductoId id, Money nuevoPrecio) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        producto.cambiarPrecio(nuevoPrecio);

        return productoRepository.save(producto);
    }

    public void activar(ProductoId id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        producto.activar();
        productoRepository.save(producto);
    }

    public void desactivar(ProductoId id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        producto.desactivar();
        productoRepository.save(producto);
    }

    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    public List<Producto> listarPorCategoria(CategoriaId categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    public void eliminar(ProductoId id) {
        productoRepository.deleteById(id);
    }
}