package com.uamishop.catalogo.api;

import com.uamishop.shared.domain.Money;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class CatalogoApiHttpClient implements CatalogoApi {

    private final RestTemplate restTemplate;
    private final String catalogoBaseUrl;

    public CatalogoApiHttpClient(RestTemplate restTemplate,
                                 @Value("${catalogo.service.url}") String catalogoBaseUrl) {
        this.restTemplate = restTemplate;
        this.catalogoBaseUrl = catalogoBaseUrl;
    }

    @Override
    @CircuitBreaker(name = "catalogoService", fallbackMethod = "fallbackObtenerProducto")
    public ProductoResumen obtenerProducto(UUID productoId) {
        String url = catalogoBaseUrl + "/api/v1/productos/" + productoId;
        try {
            ResponseEntity<ProductoResponse> response = restTemplate.getForEntity(url, ProductoResponse.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Producto no encontrado: " + productoId);
            }

            return mapear(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Producto no encontrado: " + productoId);
        }
    }

    public ProductoResumen fallbackObtenerProducto(UUID productoId, Throwable t) {
        throw new RuntimeException("Servicio de catálogo no disponible temporalmente");
    }

    @Override
    public List<ProductoResumen> listarProductos() {
        String url = catalogoBaseUrl + "/api/productos";
        try {
            ResponseEntity<ProductoResponse[]> response = restTemplate.getForEntity(url, ProductoResponse[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return Collections.emptyList();
            }

            return Arrays.stream(response.getBody())
                    .map(this::mapear)
                    .toList();
        } catch (HttpClientErrorException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<ProductoResumen> listarPorCategoria(UUID categoriaId) {
        String url = catalogoBaseUrl + "/api/productos?categoriaId=" + categoriaId;
        try {
            ResponseEntity<ProductoResponse[]> response = restTemplate.getForEntity(url, ProductoResponse[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return Collections.emptyList();
            }

            return Arrays.stream(response.getBody())
                    .map(this::mapear)
                    .toList();
        } catch (HttpClientErrorException e) {
            return Collections.emptyList();
        }
    }

    private ProductoResumen mapear(ProductoResponse dto) {
        // Usamos dto.id() y dto.activo() porque así viene en tu JSON de catálogo
        return new ProductoResumen(
                dto.id(), 
                dto.nombre(),
                dto.descripcion(),
                Money.pesos(dto.precio().doubleValue()),
                dto.activo());
    }

    // DTO corregido para coincidir con el JSON real del microservicio Catálogo
    private record ProductoResponse(
        UUID id, 
        String nombre,
        String descripcion,
        java.math.BigDecimal precio, 
        boolean activo) {
    }
}