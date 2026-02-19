package com.uamishop.backend.catalogo.infrastructure.persistence;

import com.uamishop.backend.catalogo.domain.*;
import com.uamishop.backend.catalogo.repository.ProductoRepository;
import com.uamishop.backend.shared.domain.Money;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProductoRepositoryImpl implements ProductoRepository {

    private final JpaProductoRepository jpaRepository;

    public ProductoRepositoryImpl(JpaProductoRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Producto save(Producto producto) {
        ProductoEntity entity = toEntity(producto);
        ProductoEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Producto> findById(ProductoId id) {
        return jpaRepository.findById(id.valor())
                .map(this::toDomain);
    }

    @Override
    public List<Producto> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Producto> findByCategoriaId(CategoriaId categoriaId) {
        return jpaRepository.findByCategoriaId(categoriaId.valor())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(ProductoId id) {
        jpaRepository.deleteById(id.valor());
    }

    @Override
    public boolean existsById(ProductoId id) {
        return jpaRepository.existsById(id.valor());
    }

    // MAPPERS

    private ProductoEntity toEntity(Producto producto) {
        return new ProductoEntity(
                producto.getId().valor(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio().getCantidad(),
                producto.getCategoriaId().valor(),
                producto.isDisponible(),
                producto.getFechaCreacion()
        );
    }

    private Producto toDomain(ProductoEntity entity) {
        return Producto.reconstruir(
                new ProductoId(entity.getId()),
                entity.getNombre(),
                entity.getDescripcion(),
                Money.pesos(entity.getPrecio().doubleValue()),
                new CategoriaId(entity.getCategoriaId()),
                entity.isDisponible(),
                entity.getFechaCreacion()
        );
    }
}
