/**
 * @file ProductoRepository.java
 * @brief Contrato del repositorio de dominio para el agregado Producto.
 *
 * Esta interfaz pertenece a la capa de Dominio (Domain Layer).
 *
 * Define las operaciones que pueden realizarse sobre el agregado
 * raíz Producto sin depender de ninguna tecnología de persistencia.
 *
 * Patrón aplicado:
 * - Repository Pattern
 *
 * Principio aplicado:
 * - Inversión de Dependencias (SOLID)
 *
 * Importante:
 * Esta interfaz NO debe depender de Spring, JPA ni infraestructura.
 * Solo trabaja con objetos del dominio.
 */

package com.uamishop.backend.catalogo.repository;

import com.uamishop.backend.catalogo.domain.*;
import java.util.List;
import java.util.Optional;

/**
 * @interface ProductoRepository
 *
 * Define el contrato que deben implementar las clases
 * encargadas de persistir el agregado Producto.
 *
 * Las implementaciones concretas se ubican en la capa
 * de infraestructura (por ejemplo, usando JPA).
 */
public interface ProductoRepository {

    /**
     * Guarda un producto.
     *
     * Puede representar:
     * - Creación (INSERT)
     * - Actualización (UPDATE)
     *
     * @param producto agregado raíz del dominio
     * @return producto persistido
     */
    Producto save(Producto producto);

    /**
     * Busca un producto por su identificador.
     *
     * @param id identificador del producto (Value Object)
     * @return Optional con el producto si existe
     */
    Optional<Producto> findById(ProductoId id);

    /**
     * Obtiene todos los productos almacenados.
     *
     * @return lista de productos del dominio
     */
    List<Producto> findAll();

    /**
     * Busca productos pertenecientes a una categoría específica.
     *
     * @param categoriaId identificador de la categoría
     * @return lista de productos asociados a esa categoría
     */
    List<Producto> findByCategoriaId(CategoriaId categoriaId);

    /**
     * Elimina un producto por su identificador.
     *
     * @param id identificador del producto
     */
    void deleteById(ProductoId id);

    /**
     * Verifica si un producto existe.
     *
     * @param id identificador del producto
     * @return true si existe, false en caso contrario
     */
    boolean existsById(ProductoId id);
}