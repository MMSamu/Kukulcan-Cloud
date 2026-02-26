package com.uamishop.backend.catalogo.service;

import com.uamishop.backend.catalogo.api.CatalogoApi;
import com.uamishop.backend.catalogo.domain.Producto;
import com.uamishop.backend.catalogo.domain.Imagen;
import com.uamishop.backend.catalogo.api.ProductoResumen;
import com.uamishop.backend.catalogo.repository.ProductoRepository;
import com.uamishop.backend.shared.domain.CategoriaId;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación interna de la API pública del módulo.
 * NO debe ser usada directamente por otros módulos.
 */
@Service
class CatalogoService implements CatalogoApi {

    private final ProductoRepository productoRepository;

    public CatalogoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public ProductoResumen obtenerProducto(UUID productoId) {

        Producto producto = productoRepository
                .findById(new Imagen.ProductoId(productoId))
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return mapear(producto);
    }

    @Override
    public List<ProductoResumen> listarProductos() {
        return productoRepository.findAll()
                .stream()
                .map(this::mapear)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoResumen> listarPorCategoria(UUID categoriaId) {

        return productoRepository
                .findByCategoriaId(new CategoriaId(categoriaId))
                .stream()
                .map(this::mapear)
                .collect(Collectors.toList());
    }

    private ProductoResumen mapear(Producto producto) {
        return new ProductoResumen(
                producto.getId().getValue(),  // convertimos a UUID
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.isDisponible()
        );
    }
}