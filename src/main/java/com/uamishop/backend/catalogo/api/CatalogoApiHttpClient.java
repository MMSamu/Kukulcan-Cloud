package com.uamishop.backend.catalogo.api;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.uamishop.backend.shared.domain.Money;

/**
 * Implementación de CatalogoApi que consume el microservicio de Catálogo vía
 * HTTP. Funciona como un adaptador (doble): implementa la misma interfaz
 * CatalogoApi que el resto del sistema ya conoce, sin que los otros módulos
 * necesiten saber cómo se obtienen los datos.
 *
 * Se activa cuando Catálogo está externalizado (perfil distinto a
 * catalogo-local).
 */
@Component
public class CatalogoApiHttpClient implements CatalogoApi {

    private final RestTemplate restTemplate;
    private final String catalogoBaseUrl;

    public CatalogoApiHttpClient(RestTemplate restTemplate,
            @Value("${catalogo.service.url}") String catalogoBaseUrl) {
        this.restTemplate = restTemplate;
        this.catalogoBaseUrl = catalogoBaseUrl;
    }

    // -------------------------------------------------------------------------
    // Métodos de la interfaz CatalogoApi
    // -------------------------------------------------------------------------

    @Override
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

    @Override
    public List<ProductoResumen> listarProductos() {
        String url = catalogoBaseUrl + "/api/v1/productos";
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
        String url = catalogoBaseUrl + "/api/v1/productos?categoriaId=" + categoriaId;
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

    // -------------------------------------------------------------------------
    // Mapeo: DTO de respuesta HTTP -> ProductoResumen (contrato público)
    // -------------------------------------------------------------------------

    private ProductoResumen mapear(ProductoResponse dto) {
        return new ProductoResumen(
                dto.productoId(),
                dto.nombre(),
                dto.descripcion(),
                Money.pesos(dto.precio()),
                dto.disponible());
    }

    // -------------------------------------------------------------------------
    // DTO interno para deserializar la respuesta JSON del microservicio.
    // Es privado: ningún otro módulo lo conoce ni depende de él.
    // -------------------------------------------------------------------------

    private record ProductoResponse(
            UUID productoId,
            String nombre,
            String descripcion,
            double precio,
            boolean disponible) {
    }
}
