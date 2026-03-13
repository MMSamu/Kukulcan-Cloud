package com.uamishop.backend.ventas.api;

import com.uamishop.backend.catalogo.domain.Categoria;
import com.uamishop.backend.catalogo.domain.Producto;
import com.uamishop.backend.catalogo.domain.Imagen;
import com.uamishop.backend.catalogo.repository.CategoriaRepository;
import com.uamishop.backend.catalogo.repository.ProductoRepository;
import com.uamishop.backend.shared.domain.CategoriaId;
import com.uamishop.backend.shared.domain.ClienteId;
import com.uamishop.backend.shared.domain.ProductoId;
import com.uamishop.backend.shared.domain.Money;
import com.uamishop.backend.ventas.domain.Carrito;
import com.uamishop.backend.ventas.service.CarritoService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VentasApiIntegrationTest {

    @Autowired 
    private VentasApi ventasApi; // Inyectamos la API para probarla directamente
    
    @Autowired 
    private CarritoService carritoService; // Inyectamos el servicio para manipular el carrito durante las pruebas

    @Autowired 
    private ProductoRepository productoRepository; // Inyectamos el repositorio para crear productos de prueba

    @Autowired 
    private CategoriaRepository categoriaRepository; // Inyectamos el repositorio para crear categorías de prueba

    @Autowired 
    private EntityManager entityManager; // Inyectamos el EntityManager para manejar transacciones y persistencia durante las pruebas

    @Test
    void debeObtenerResumenCorrectamente() {
        // GIVEN: Un producto activo con imagen y un carrito con ese producto
        CategoriaId catId = new CategoriaId(UUID.randomUUID());
        categoriaRepository.save(new Categoria(catId, "General", "Desc"));

        // Creamos un producto con una imagen y lo activamos
        ProductoId idCata = ProductoId.generar();
        Producto producto = Producto.reconstruir(idCata, "Laptop", "Desc", Money.pesos(100.0), catId, false, LocalDateTime.now());
        producto.agregarImagen(new Imagen("https://uami.mx/t.jpg", "T", 1));
        producto.activar();
        productoRepository.save(producto);
        entityManager.flush(); // Sirve para asegurar que el producto se guarde en la base de datos antes de continuar con la prueba

        Carrito carrito = carritoService.crear(ClienteId.de(UUID.randomUUID()));
        carritoService.agregarProducto(carrito.getId(), new ProductoId(idCata.valor()), 2);

        // WHEN: Se solicita el resumen del carrito a la API
        CarritoResumen resumen = ventasApi.obtenerResumen(carrito.getId().value());

        // THEN: El resumen contiene los productos y cantidades correctas
        assertNotNull(resumen);
        assertEquals(1, resumen.items().size());
        assertEquals(2, resumen.items().get(0).cantidad());
    }

    @Test
    void debeCompletarCheckoutPublico() {
        // GIVEN: Un carrito en estado CHECKOUT con productos disponibles
        CategoriaId catId = new CategoriaId(UUID.randomUUID());
        categoriaRepository.save(new Categoria(catId, "General", "Desc"));

        // Creamos un producto con una imagen y lo activamos
        ProductoId idCata = ProductoId.generar();
        Producto producto = Producto.reconstruir(idCata, "Mouse", "Desc", Money.pesos(50.0), catId, false, LocalDateTime.now());
        producto.agregarImagen(new Imagen("https://uami.mx/t.jpg", "T", 1));
        producto.activar();
        productoRepository.save(producto);
        entityManager.flush(); // Sirve para asegurar que el producto se guarde en la base de datos antes de continuar con la prueba

        Carrito carrito = carritoService.crear(ClienteId.de(UUID.randomUUID()));
        carritoService.agregarProducto(carrito.getId(), new ProductoId(idCata.valor()), 1);
        carritoService.iniciarCheckout(carrito.getId());

        // WHEN: Se completa el checkout a través de la API pública
        ventasApi.completarCheckoutPublico(carrito.getId().value());

        // THEN: El estado del carrito cambia a COMPLETADO
        CarritoResumen resumenFinal = ventasApi.obtenerResumen(carrito.getId().value());
        assertEquals("COMPLETADO", resumenFinal.estado());
    }
}