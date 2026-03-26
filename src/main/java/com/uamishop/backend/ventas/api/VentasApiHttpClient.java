package com.uamishop.backend.ventas.api;

import com.uamishop.backend.shared.domain.ClienteId;
import com.uamishop.backend.shared.domain.Money;
import com.uamishop.backend.shared.domain.ProductoId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
@Profile("!ventas-local")
public class VentasApiHttpClient implements VentasApi {

    private final RestTemplate restTemplate;
    private final String ventasBaseUrl;

    public VentasApiHttpClient(
            RestTemplate restTemplate,
            @Value("${ventas.service.url}") String ventasBaseUrl) {
        this.restTemplate = restTemplate;
        this.ventasBaseUrl = ventasBaseUrl;
    }

    @Override
    public CarritoResumen obtenerResumen(UUID carritoId) {
        String url = ventasBaseUrl + "/api/v1/carritos/" + carritoId;

        try {
            ResponseEntity<CarritoResponse> response =
                    restTemplate.getForEntity(url, CarritoResponse.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("No fue posible obtener el resumen del carrito: " + carritoId);
            }

            return mapear(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Carrito no encontrado: " + carritoId);
        }
    }

    @Override
    public void completarCheckoutPublico(UUID carritoId) {
        String url = ventasBaseUrl + "/api/v1/carritos/" + carritoId + "/completar";

        try {
            restTemplate.postForEntity(url, null, Void.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Carrito no encontrado para completar checkout: " + carritoId);
        }
    }

    private CarritoResumen mapear(CarritoResponse carrito) {
        List<CarritoResumen.ItemCarritoResumen> items = carrito.items().stream()
                .map(item -> new CarritoResumen.ItemCarritoResumen(
                        new ProductoId(item.productoId()),
                        item.nombreProducto(),
                        item.sku(),
                        item.cantidad(),
                        Money.pesos(item.precioUnitario().doubleValue())
                ))
                .toList();

        return new CarritoResumen(
                carrito.id(),
                ClienteId.de(carrito.clienteId()),
                carrito.estado(),
                items
        );
    }

    private record CarritoResponse(
            UUID id,
            UUID clienteId,
            List<ItemResponse> items,
            BigDecimal subtotal,
            BigDecimal descuento,
            BigDecimal total,
            String estado
    ) {
    }

    private record ItemResponse(
            UUID productoId,
            String nombreProducto,
            String sku,
            int cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal
    ) {
    }
}