package com.uamishop.backend.catalogo.service;

import com.uamishop.backend.catalogo.domain.ProductoEstadisticas;
import com.uamishop.backend.catalogo.repository.ProductoEstadisticasJpaRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ProductoEstadisticasService {

    private final ProductoEstadisticasJpaRepository repository;

    public ProductoEstadisticasService(ProductoEstadisticasJpaRepository repository) {
        this.repository = repository;
    }

    /**
     * Registrar una venta de producto
     */
    public void registrarVenta(UUID productoId, int cantidad) {

        ProductoEstadisticas stats = repository.findById(productoId)
                .orElse(new ProductoEstadisticas(productoId));

        stats.setVentasTotales(stats.getVentasTotales() + 1);
        stats.setCantidadVendida(stats.getCantidadVendida() + cantidad);
        stats.setUltimaVentaAt(Instant.now());

        repository.save(stats);
    }

    /**
     * Registrar cuando un producto se agrega al carrito
     */
    public void registrarAgregadoAlCarrito(UUID productoId) {

        ProductoEstadisticas stats = repository.findById(productoId)
                .orElse(new ProductoEstadisticas(productoId));

        stats.setVecesAgregadoAlCarrito(
                stats.getVecesAgregadoAlCarrito() + 1
        );

        stats.setUltimaAgregadoAlCarritoAt(Instant.now());

        repository.save(stats);
    }

    /**
     * Obtener productos más vendidos
     */
    public List<ProductoEstadisticas> obtenerMasVendidos(int limit) {
        return repository.findMasVendidos(limit);
    }

    /**
     * Obtener estadísticas de un producto
     */
    public ProductoEstadisticas obtenerEstadisticas(UUID productoId) {
        return repository.findById(productoId).orElse(null);
    }
}
