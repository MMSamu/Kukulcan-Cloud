package com.uamishop.backend.orden.api;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.uamishop.backend.orden.domain.DireccionEnvio;
import com.uamishop.backend.shared.domain.Money;

/**
 * Implementación de OrdenesApi que consume el microservicio de Órdenes vía
 * HTTP. Funciona como un adaptador (doble): implementa la misma interfaz
 * OrdenesApi que el resto del sistema ya conoce, sin que los otros módulos
 * necesiten saber cómo se obtienen los datos.
 *
 * Se activa cuando Órdenes está externalizado (perfil distinto a
 * ordenes-local).
 */
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class OrdenApiHttpClient implements OrdenesApi {

    private final RestTemplate restTemplate;
    private final String ordenesBaseUrl;

    public OrdenApiHttpClient(RestTemplate restTemplate,
            @Value("${ordenes.service.url}") String ordenesBaseUrl) {
        this.restTemplate = restTemplate;
        this.ordenesBaseUrl = ordenesBaseUrl;
    }

    // -------------------------------------------------------------------------
    // Métodos de la interfaz OrdenesApi — Consultas
    // -------------------------------------------------------------------------

    @Override
    public OrdenResumen obtenerOrden(UUID ordenId) {
        String url = ordenesBaseUrl + "/api/v2/ordenes/" + ordenId;
        try {
            ResponseEntity<OrdenResponse> response = restTemplate.getForEntity(url, OrdenResponse.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Orden no encontrada: " + ordenId);
            }

            return mapearOrden(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Orden no encontrada: " + ordenId);
        }
    }

    @Override
    public List<OrdenResumen> listarOrdenes() {
        String url = ordenesBaseUrl + "/api/v2/ordenes";
        try {
            ResponseEntity<OrdenResponse[]> response = restTemplate.getForEntity(url, OrdenResponse[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return Collections.emptyList();
            }

            return Arrays.stream(response.getBody())
                    .map(this::mapearOrden)
                    .toList();
        } catch (HttpClientErrorException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<DatosResumen> listarDatos() {
        String url = ordenesBaseUrl + "/api/v2/ordenes/datos";
        try {
            ResponseEntity<DatosResponse[]> response = restTemplate.getForEntity(url, DatosResponse[].class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return Collections.emptyList();
            }

            return Arrays.stream(response.getBody())
                    .map(this::mapearDatos)
                    .toList();
        } catch (HttpClientErrorException e) {
            return Collections.emptyList();
        }
    }

    // -------------------------------------------------------------------------
    // Métodos de la interfaz OrdenesApi — Comandos
    // -------------------------------------------------------------------------

    @Override
    public OrdenResumen crear(UUID clienteId, DireccionEnvio direccionEnvio) {
        String url = ordenesBaseUrl + "/api/v2/ordenes";
        Map<String, Object> body = Map.of("clienteId", clienteId);

        ResponseEntity<OrdenResponse> response = restTemplate.postForEntity(url, body, OrdenResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Error al crear orden para cliente: " + clienteId);
        }

        return mapearOrden(response.getBody());
    }

    @Override
    public OrdenResumen crearDesdeCarrito(UUID carritoId, DireccionEnvio direccionEnvio) {
        String url = ordenesBaseUrl + "/api/v2/ordenes/" + carritoId + "/orden";

        Map<String, String> body = Map.of(
                "calle", direccionEnvio.getCalle(),
                "ciudad", direccionEnvio.getCiudad(),
                "estado", direccionEnvio.getEstado(),
                "codigoPostal", direccionEnvio.getCodigoPostal(),
                "telefonoContacto", direccionEnvio.getTelefonoContacto());

        ResponseEntity<OrdenResponse> response = restTemplate.postForEntity(url, body, OrdenResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Error al crear orden desde carrito: " + carritoId);
        }

        return mapearOrden(response.getBody());
    }

    @Override
    public OrdenResumen confirmar(UUID ordenId) {
        String url = ordenesBaseUrl + "/api/v2/ordenes/" + ordenId + "/confirmar";

        ResponseEntity<OrdenResponse> response = restTemplate.postForEntity(url, null, OrdenResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Error al confirmar orden: " + ordenId);
        }

        return mapearOrden(response.getBody());
    }

    @Override
    public OrdenResumen procesarPago(UUID ordenId, String referenciaPago) {
        String url = ordenesBaseUrl + "/api/v2/ordenes/" + ordenId + "/procesar-pago";
        Map<String, String> body = Map.of("referenciaPago", referenciaPago);

        ResponseEntity<OrdenResponse> response = restTemplate.postForEntity(url, body, OrdenResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Error al procesar pago de orden: " + ordenId);
        }

        return mapearOrden(response.getBody());
    }

    @Override
    public OrdenResumen marcarEnProceso(UUID ordenId) {
        String url = ordenesBaseUrl + "/api/v2/ordenes/" + ordenId + "/marcar-en-proceso";

        ResponseEntity<OrdenResponse> response = restTemplate.postForEntity(url, null, OrdenResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Error al marcar orden en proceso: " + ordenId);
        }

        return mapearOrden(response.getBody());
    }

    @Override
    public OrdenResumen marcarEnviada(UUID ordenId, String numeroGuia) {
        String url = ordenesBaseUrl + "/api/v2/ordenes/" + ordenId + "/marcar-enviada";
        Map<String, String> body = Map.of("numeroGuia", numeroGuia);

        ResponseEntity<OrdenResponse> response = restTemplate.postForEntity(url, body, OrdenResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Error al marcar orden como enviada: " + ordenId);
        }

        return mapearOrden(response.getBody());
    }

    @Override
    public OrdenResumen marcarEntregada(UUID ordenId) {
        String url = ordenesBaseUrl + "/api/v2/ordenes/" + ordenId + "/marcar-entregada";

        ResponseEntity<OrdenResponse> response = restTemplate.postForEntity(url, null, OrdenResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Error al marcar orden como entregada: " + ordenId);
        }

        return mapearOrden(response.getBody());
    }

    @Override
    public OrdenResumen cancelar(UUID ordenId, String motivo) {
        String url = ordenesBaseUrl + "/api/v2/ordenes/" + ordenId + "/cancelar";
        Map<String, String> body = Map.of("motivo", motivo);

        ResponseEntity<OrdenResponse> response = restTemplate.postForEntity(url, body, OrdenResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Error al cancelar orden: " + ordenId);
        }

        return mapearOrden(response.getBody());
    }

    // -------------------------------------------------------------------------
    // Mapeo: DTO de respuesta HTTP -> OrdenResumen (contrato público)
    // -------------------------------------------------------------------------

    private OrdenResumen mapearOrden(OrdenResponse dto) {
        return new OrdenResumen(
                dto.id(),
                dto.clienteId(),
                dto.estado(),
                Money.pesos(dto.subtotal()),
                Money.pesos(dto.descuento()),
                Money.pesos(dto.total()),
                dto.fechaCreacion());
    }

    private DatosResumen mapearDatos(DatosResponse dto) {
        return new DatosResumen(
                dto.clienteId(),
                dto.nombre(),
                dto.apellido(),
                dto.estado(),
                dto.direccion(),
                dto.telefono(),
                dto.metodoPago(),
                dto.formaPago(),
                dto.fecha(),
                dto.hora());
    }

    // -------------------------------------------------------------------------
    // DTOs internos para deserializar las respuestas JSON del microservicio.
    // Son privados: ningún otro módulo los conoce ni depende de ellos.
    // -------------------------------------------------------------------------

    private record OrdenResponse(
            UUID id,
            UUID clienteId,
            String estado,
            double subtotal,
            double descuento,
            double total,
            LocalDateTime fechaCreacion,
            LocalDateTime fechaActualizacion) {
    }

    private record DatosResponse(
            UUID clienteId,
            String nombre,
            String apellido,
            String estado,
            String direccion,
            String telefono,
            String metodoPago,
            String formaPago,
            String fecha,
            String hora) {
    }
}
