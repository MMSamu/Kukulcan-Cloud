/**
 * @file ProductoRepositoryImpl.java
 * @brief Implementación del repositorio de dominio Producto.
 *
 * Esta clase pertenece a la capa de Infrastructure.
 *
 * Implementa la interfaz ProductoRepository (definida en el dominio)
 * y actúa como adaptador entre:
 *
 *   - El modelo de dominio (Producto, ProductoId, CategoriaId, Money)
 *   - La infraestructura de persistencia (JpaProductoRepository, ProductoEntity)
 *
 * Patrón aplicado:
 * - Repository Pattern
 * - Adapter Pattern
 *
 * Responsabilidad principal:
 * Traducir entre el modelo de dominio y la entidad JPA.
 */

package com.uamishop.backend.catalogo.infrastructure.persistence;

import com.uamishop.backend.catalogo.domain.*;
import com.uamishop.backend.catalogo.repository.ProductoRepository;
import com.uamishop.backend.shared.domain.CategoriaId;
import com.uamishop.backend.shared.domain.Money;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @class ProductoRepositoryImpl
 *
 * Implementación concreta del repositorio de dominio Producto.
 *
 * Está anotada con @Repository para que Spring la detecte
 * automáticamente como componente gestionado.
 */
@Repository
public class ProductoRepositoryImpl implements ProductoRepository {

    /**
     * Repositorio JPA proporcionado por Spring Data.
     *
     * Se utiliza para realizar las operaciones CRUD reales
     * contra la base de datos.
     */
    private final JpaProductoRepository jpaRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param jpaRepository repositorio JPA generado por Spring
     */
    public ProductoRepositoryImpl(JpaProductoRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * Guarda un producto en la base de datos.
     *
     * Flujo:
     * 1. Convierte el objeto de dominio a entidad JPA.
     * 2. Llama a jpaRepository.save().
     * 3. Convierte el resultado nuevamente a dominio.
     *
     * @param producto agregado raíz del dominio
     * @return producto persistido
     */
    @Override
    public Producto save(Producto producto) {
        ProductoEntity entity = toEntity(producto);
        ProductoEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    /**
     * Busca un producto por su identificador.
     *
     * @param id identificador del producto (Value Object)
     * @return Optional con el producto si existe
     */
    @Override
    public Optional<Producto> findById(Imagen.ProductoId id) {
        return jpaRepository.findById(id.valor())
                .map(this::toDomain);
    }

    /**
     * Obtiene todos los productos almacenados.
     *
     * @return lista de productos del dominio
     */
    @Override
    public List<Producto> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Busca productos pertenecientes a una categoría específica.
     *
     * Utiliza el método derivado de Spring Data:
     * findByCategoriaId(UUID categoriaId)
     *
     * @param categoriaId identificador de la categoría
     * @return lista de productos asociados
     */
    @Override
    public List<Producto> findByCategoriaId(CategoriaId categoriaId) {
        return jpaRepository.findByCategoriaId(categoriaId.valor())
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Elimina un producto por su identificador.
     *
     * @param id identificador del producto
     */
    @Override
    public void deleteById(Imagen.ProductoId id) {
        jpaRepository.deleteById(id.valor());
    }

    /**
     * Verifica si un producto existe en la base de datos.
     *
     * @param id identificador del producto
     * @return true si existe, false en caso contrario
     */
    @Override
    public boolean existsById(Imagen.ProductoId id) {
        return jpaRepository.existsById(id.valor());
    }

    // =====================================================
    // MAPPERS
    // =====================================================

    /**
     * Convierte un agregado de dominio Producto
     * en una entidad JPA ProductoEntity.
     *
     * Aquí se realiza la transformación de:
     * - Value Objects → Tipos primitivos persistibles
     *
     * @param producto objeto de dominio
     * @return entidad JPA
     */
    private ProductoEntity toEntity(Producto producto) {
        return new ProductoEntity(
                producto.getId().valor(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio().getCantidad(), // Money → BigDecimal
                producto.getCategoriaId().valor(),
                producto.isDisponible(),
                producto.getFechaCreacion()
        );
    }

    /**
     * Convierte una entidad JPA en un agregado de dominio.
     *
     * Aquí se reconstruye el objeto del dominio utilizando:
     * - ProductoId
     * - CategoriaId
     * - Money
     *
     * Se utiliza el método estático Producto.reconstruir()
     * para evitar saltarse invariantes del dominio.
     *
     * @param entity entidad JPA
     * @return agregado Producto del dominio
     */
    private Producto toDomain(ProductoEntity entity) {
        return Producto.reconstruir(
                new Imagen.ProductoId(entity.getId()),
                entity.getNombre(),
                entity.getDescripcion(),
                Money.pesos(entity.getPrecio().doubleValue()),
                new CategoriaId(entity.getCategoriaId()),
                entity.isDisponible(),
                entity.getFechaCreacion()
        );
    }
}