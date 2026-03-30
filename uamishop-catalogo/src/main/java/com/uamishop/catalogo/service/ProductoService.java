package com.uamishop.catalogo.service;

import com.uamishop.catalogo.repository.ProductoRepository;
import com.uamishop.catalogo.repository.CategoriaRepository;
import com.uamishop.catalogo.shared.domain.ProductoId;
import com.uamishop.catalogo.shared.domain.CategoriaId;
import com.uamishop.catalogo.shared.domain.Money;
import com.uamishop.catalogo.exception.BusinessRuleException;
import com.uamishop.catalogo.controller.dto.ProductoRequest;
import com.uamishop.catalogo.controller.dto.ProductoResponse;
import com.uamishop.catalogo.domain.Producto;
import com.uamishop.catalogo.domain.Categoria;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@org.springframework.transaction.annotation.Transactional
public class ProductoService {

        private final ProductoRepository productoRepository;
        private final CategoriaRepository categoriaRepository;
        private final ProductoEstadisticasService estadisticasService;

        public ProductoService(
                        ProductoRepository productoRepository,
                        CategoriaRepository categoriaRepository,
                        ProductoEstadisticasService estadisticasService) {
                this.productoRepository = productoRepository;
                this.categoriaRepository = categoriaRepository;
                this.estadisticasService = estadisticasService;
        }

        // =====================================================
        // CREAR PRODUCTO
        // =====================================================

        public ProductoResponse crear(@Valid ProductoRequest request) {

                // Validación de regla de negocio: precio obligatorio y mayor a 0
                if (request.precio() == null || request.precio().doubleValue() <= 0) {
                        throw new BusinessRuleException(
                                        "PRECIO_INVALIDO",
                                        "El precio del producto debe ser mayor a cero");
                }

                // Validar que la categoría exista
                Categoria categoria = categoriaRepository.findById(
                                new CategoriaId(request.categoriaId())).orElseThrow(
                                                () -> new BusinessRuleException(
                                                                "CATEGORIA_NO_ENCONTRADA",
                                                                "La categoría especificada no existe"));

                // Crear producto desde el dominio
                Producto producto = Producto.crear(
                                request.nombre(),
                                request.descripcion(),
                                Money.pesos(request.precio().doubleValue()),
                                categoria.getId());

                productoRepository.save(producto);

                return toResponse(producto);
        }

        // =====================================================
        // ACTUALIZAR PRODUCTO
        // =====================================================

        public ProductoResponse actualizar(UUID id, ProductoRequest request) {

                Producto producto = productoRepository.findById(
                                new ProductoId(id)).orElseThrow(
                                                () -> new BusinessRuleException(
                                                                "PRODUCTO_NO_ENCONTRADO",
                                                                "El producto no existe"));

                producto.actualizarNombreYDescripcion(
                                request.nombre(),
                                request.descripcion());

                if (request.precio() != null) {

                        if (request.precio().doubleValue() <= 0) {
                                throw new BusinessRuleException(
                                                "PRECIO_INVALIDO",
                                                "El precio del producto debe ser mayor a cero");
                        }

                        producto.cambiarPrecio(Money.pesos(request.precio().doubleValue()));
                }
                if (request.categoriaId() != null) {
                        CategoriaId nuevaId = new CategoriaId(request.categoriaId());

                        // Verificamos que exista la categoría antes de asignarla
                        categoriaRepository.findById(nuevaId)
                                .orElseThrow(() -> new BusinessRuleException("CATEGORIA_NOT_FOUND", "La nueva categoría no existe"));
                        
                        // Aquí es donde se usa el método que agregamos
                        producto.cambiarCategoria(nuevaId);
                }

                Producto guardado = productoRepository.save(producto);
                return toResponse(guardado);
    }

        // =====================================================
        // ACTIVAR PRODUCTO
        // =====================================================

        public void activar(UUID id) {
                // Buscamos el producto usando el Value Object ProductoId
                Producto producto = productoRepository.findById(new ProductoId(id))
                        .orElseThrow(() -> new EntityNotFoundException("No se encontró el producto con ID: " + id));
                
                // Ahora esto ya no lanzará la excepción de "falta imagen"
                producto.activar();
                
                // Guardamos los cambios (esto disparará el evento para que Ventas se entere)
                productoRepository.save(producto);
        }

        // =====================================================
        // DESACTIVAR PRODUCTO
        // =====================================================

        public void desactivar(UUID id) {

                Producto producto = productoRepository.findById(
                                new ProductoId(id)).orElseThrow(
                                                () -> new BusinessRuleException(
                                                                "PRODUCTO_NO_ENCONTRADO",
                                                                "El producto no existe"));

                producto.desactivar();
                productoRepository.save(producto);
        }

        // =====================================================
        // OBTENER POR ID
        // =====================================================

        public ProductoResponse obtenerPorId(UUID id) {

                Producto producto = productoRepository.findById(
                                new ProductoId(id)).orElseThrow(
                                                () -> new BusinessRuleException(
                                                                "PRODUCTO_NO_ENCONTRADO",
                                                                "El producto no existe"));

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
                                producto.getId().getValor(),
                                producto.getNombre(),
                                producto.getDescripcion(),
                                producto.getPrecio().getCantidad(),
                                producto.getCategoriaId().valor(),
                                producto.isDisponible());
        }
}
