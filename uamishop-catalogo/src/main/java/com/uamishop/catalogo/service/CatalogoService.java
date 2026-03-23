package com.uamishop.catalogo.service;

import com.uamishop.catalogo.domain.Producto;
import com.uamishop.catalogo.shared.domain.ProductoId;
//import com.uamishop.catalogo.api.ProductoResumen;
import com.uamishop.catalogo.repository.ProductoRepository;
import com.uamishop.catalogo.shared.domain.CategoriaId;
import com.uamishop.catalogo.controller.dto.ProductoResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio interno del microservicio de catálogo.
 */
@Service
public class CatalogoService {

    private final ProductoRepository productoRepository;

    public CatalogoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public ProductoResponse obtenerProducto(UUID productoId) {

        Producto producto = productoRepository
                .findById(new ProductoId(productoId))
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return mapear(producto);
    }

    public List<ProductoResponse> listarProductos() {
        return productoRepository.findAll()
                .stream()
                .map(this::mapear)
                .collect(Collectors.toList());
    }

    public List<ProductoResponse> listarPorCategoria(UUID categoriaId) {

        return productoRepository
                .findByCategoriaId(new CategoriaId(categoriaId))
                .stream()
                .map(this::mapear)
                .collect(Collectors.toList());
    }

    private ProductoResponse mapear(Producto producto) {
        return new ProductoResponse(
                producto.getId().getValor(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio().getCantidad(),
                producto.getCategoriaId().valor(),
                producto.isDisponible()
        );
    }
}