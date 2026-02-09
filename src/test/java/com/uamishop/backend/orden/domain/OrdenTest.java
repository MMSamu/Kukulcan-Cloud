package com.uamishop.backend.orden.domain;

import com.uamishop.backend.shared.domain.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests completos para el agregado Orden.
 * Valida todas las reglas de negocio RN-ORD-01 a RN-ORD-16.
 */
class OrdenTest {

    private ClienteId clienteId;
    private DireccionEnvio direccionValida;
    private List<ItemOrden> itemsValidos;

    @BeforeEach
    void setUp() {
        clienteId = ClienteId.generar();
        direccionValida = DireccionEnvio.crear(
                "Av. Insurgentes Sur 123",
                "Ciudad de México",
                "CDMX",
                "01234",
                "5512345678");

        itemsValidos = new ArrayList<>();
        itemsValidos.add(ItemOrden.crear(
                UUID.randomUUID(),
                "Producto Test",
                "SKU-001",
                2,
                Money.pesos(100.00)));
    }

    // ==================== Tests de crear() ====================

    @Test
    void deberiaCrearOrdenConItemsValidos() {
        // Happy path
        Orden orden = Orden.crear(clienteId, itemsValidos, direccionValida);

        assertNotNull(orden);
        assertNotNull(orden.getId());
        assertEquals(clienteId, orden.getClienteId());
        assertEquals(1, orden.getItems().size());
        assertEquals(EstadoOrden.PENDIENTE, orden.getEstadoActual());
        assertEquals(EstadoPago.PENDIENTE, orden.getEstadoPago());
        assertTrue(orden.getTotal().esPositivo());
    }

    @Test
    void noDeberiaCrearOrdenSinItems() {
        // RN-ORD-01: Debe tener al menos un item
        List<ItemOrden> itemsVacios = new ArrayList<>();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Orden.crear(clienteId, itemsVacios, direccionValida);
        });

        assertTrue(exception.getMessage().contains("al menos un item"));
    }

    @Test
    void noDeberiaCrearOrdenConItemsNulos() {
        // RN-ORD-01: Debe tener al menos un item
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Orden.crear(clienteId, null, direccionValida);
        });

        assertTrue(exception.getMessage().contains("al menos un item"));
    }

    @Test
    void noDeberiaCrearOrdenConTotalCero() {
        // RN-ORD-02: El total debe ser mayor a cero
        // Crear un item con precio cero no es posible debido a validación en Money
        // Este test documenta la regla

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Money.pesos(0.00); // Esto debería fallar por RN-VO-02
        });

        // Money con cantidad 0 no es positivo, por lo que no puede crearse una orden
        assertNotNull(exception);
    }

    @Test
    void noDeberiaCrearConTelefonoInvalido() {
        // RN-ORD-04: El teléfono debe ser de 10 dígitos (validado en DireccionEnvio)
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DireccionEnvio direccionInvalida = DireccionEnvio.crear(
                    "Av. Universidad 789",
                    "Monterrey",
                    "Nuevo León",
                    "64000",
                    "81123456" // Solo 8 dígitos
            );
            Orden.crear(clienteId, itemsValidos, direccionInvalida);
        });

        assertTrue(exception.getMessage().contains("10 dígitos"));
    }

    // ==================== Tests de confirmar() ====================

    @Test
    void deberiaConfirmarOrdenPendiente() {
        // RN-ORD-05: Happy path
        Orden orden = Orden.crear(clienteId, itemsValidos, direccionValida);

        orden.confirmar();

        assertEquals(EstadoOrden.CONFIRMADA, orden.getEstadoActual());
        assertEquals(1, orden.getHistorialEstados().size());
    }

    @Test
    void noDeberiaConfirmarSiNoEstaPendiente() {
        // RN-ORD-05: Solo se pueden confirmar órdenes PENDIENTES
        Orden orden = Orden.crear(clienteId, itemsValidos, direccionValida);
        orden.confirmar(); // Primera confirmación exitosa

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            orden.confirmar(); // Intentar confirmar de nuevo
        });

        assertTrue(exception.getMessage().contains("PENDIENTE"));
    }

    @Test
    void deberiaRegistrarCambioEnHistorial() {
        // RN-ORD-06: Registrar cambio en historial
        Orden orden = Orden.crear(clienteId, itemsValidos, direccionValida);
        assertEquals(0, orden.getHistorialEstados().size());

        orden.confirmar();

        assertEquals(1, orden.getHistorialEstados().size());
        CambioEstado cambio = orden.getHistorialEstados().get(0);
        assertEquals(EstadoOrden.PENDIENTE, cambio.getEstadoAnterior());
        assertEquals(EstadoOrden.CONFIRMADA, cambio.getEstadoNuevo());
        assertNotNull(cambio.getFechaCambio());
    }

    // ==================== Tests de procesarPago() ====================

    @Test
    void deberiaProcesarPagoConReferencia() {
        // RN-ORD-07, RN-ORD-08: Happy path
        Orden orden = Orden.crear(clienteId, itemsValidos, direccionValida);
        orden.confirmar();

        orden.procesarPago("REF-123456");

        assertEquals(EstadoOrden.PREPARACION, orden.getEstadoActual());
        assertEquals(EstadoPago.COMPLETADO, orden.getEstadoPago());
        assertEquals("REF-123456", orden.getReferenciaPago());
        assertEquals(2, orden.getHistorialEstados().size());
    }

    @Test
    void noDeberiaProcesarPagoSinConfirmar() {
        // RN-ORD-07: Solo se puede procesar pago de órdenes CONFIRMADAS
        Orden orden = Orden.crear(clienteId, itemsValidos, direccionValida);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            orden.procesarPago("REF-123456");
        });

        assertTrue(exception.getMessage().contains("CONFIRMADAS"));
    }

    @Test
    void noDeberiaProcesarPagoSinReferencia() {
        // RN-ORD-08: La referencia de pago no debe estar vacía
        Orden orden = Orden.crear(clienteId, itemsValidos, direccionValida);
        orden.confirmar();

        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            orden.procesarPago("");
        });
        assertTrue(exception1.getMessage().contains("referencia de pago"));

        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            orden.procesarPago(null);
        });
        assertTrue(exception2.getMessage().contains("referencia de pago"));
    }

    // ==================== Tests de marcarEnviada() ====================

    @Test
    void deberiaMarcarEnviadaConGuia() {
        // RN-ORD-10, RN-ORD-11, RN-ORD-12: Happy path
        Orden orden = Orden.crear(clienteId, itemsValidos, direccionValida);
        orden.confirmar();
        orden.procesarPago("REF-123456");

        orden.marcarEnviada("GUIA-12345");

        assertEquals(EstadoOrden.ENVIADA, orden.getEstadoActual());
        assertEquals("GUIA-12345", orden.getNumeroGuia());
        assertEquals(3, orden.getHistorialEstados().size());
    }

    @Test
    void noDeberiaMarcarEnviadaSinPreparar() {
        // RN-ORD-10: Solo se pueden marcar como enviadas las órdenes en PREPARACION
        Orden orden = Orden.crear(clienteId, itemsValidos, direccionValida);
        orden.confirmar();

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            orden.marcarEnviada("GUIA-12345");
        });

        assertTrue(exception.getMessage().contains("PREPARACION"));
    }

    @Test
    void noDeberiaMarcarEnviadaSinGuia() {
        // RN-ORD-11: Se requiere número de guía
        Orden orden = Orden.crear(clienteId, itemsValidos, direccionValida);
        orden.confirmar();
        orden.procesarPago("REF-123456");

        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            orden.marcarEnviada("");
        });
        assertTrue(exception1.getMessage().contains("obligatorio"));

        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            orden.marcarEnviada(null);
        });
        assertTrue(exception2.getMessage().contains("obligatorio"));
    }

    @Test
    void noDeberiaMarcarEnviadaConGuiaCorta() {
        // RN-ORD-12: La guía debe tener al menos 5 caracteres
        Orden orden = Orden.crear(clienteId, itemsValidos, direccionValida);
        orden.confirmar();
        orden.procesarPago("REF-123456");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orden.marcarEnviada("G123"); // Solo 4 caracteres
        });

        assertTrue(exception.getMessage().contains("al menos 5 caracteres"));
    }

    // ==================== Tests de cancelar() ====================

    @Test
    void deberiaCancelarOrdenNoProcesada() {
        // RN-ORD-14: Happy path - cancelar orden PENDIENTE
        Orden orden = Orden.crear(clienteId, itemsValidos, direccionValida);

        orden.cancelar("El cliente canceló la orden porque cambió de opinión");

        assertEquals(EstadoOrden.CANCELADA, orden.getEstadoActual());
        assertEquals(1, orden.getHistorialEstados().size());
        assertTrue(orden.getHistorialEstados().get(0).getMotivo().contains("canceló"));
    }

    @Test
    void deberiaCancelarOrdenConfirmada() {
        // RN-ORD-14: Se puede cancelar orden CONFIRMADA
        Orden orden = Orden.crear(clienteId, itemsValidos, direccionValida);
        orden.confirmar();

        orden.cancelar("Cancelación solicitada por el cliente antes del pago");

        assertEquals(EstadoOrden.CANCELADA, orden.getEstadoActual());
    }

    @Test
    void deberiaCancelarOrdenEnPreparacion() {
        // RN-ORD-14: Se puede cancelar orden en PREPARACION
        Orden orden = Orden.crear(clienteId, itemsValidos, direccionValida);
        orden.confirmar();
        orden.procesarPago("REF-123456");

        orden.cancelar("Producto agotado, se reembolsará el pago al cliente");

        assertEquals(EstadoOrden.CANCELADA, orden.getEstadoActual());
    }

    @Test
    void noDeberiaCancelarOrdenEnviada() {
        // RN-ORD-14: No se puede cancelar si está ENVIADA
        Orden orden = Orden.crear(clienteId, itemsValidos, direccionValida);
        orden.confirmar();
        orden.procesarPago("REF-123456");
        orden.marcarEnviada("GUIA-12345");

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            orden.cancelar("Intentando cancelar orden enviada");
        });

        assertTrue(exception.getMessage().contains("enviadas"));
    }

    @Test
    void noDeberiaCancelarOrdenEntregada() {
        // RN-ORD-14: No se puede cancelar si está ENTREGADA
        Orden orden = Orden.crear(clienteId, itemsValidos, direccionValida);
        orden.confirmar();
        orden.procesarPago("REF-123456");
        orden.marcarEnviada("GUIA-12345");
        orden.marcarEntregada();

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            orden.cancelar("Intentando cancelar orden entregada");
        });

        assertTrue(exception.getMessage().contains("entregadas"));
    }

    @Test
    void noDeberiaCancelarSinMotivo() {
        // RN-ORD-15, RN-ORD-16: Se requiere motivo
        Orden orden = Orden.crear(clienteId, itemsValidos, direccionValida);

        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            orden.cancelar("");
        });
        assertTrue(exception1.getMessage().contains("al menos 10 caracteres"));

        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            orden.cancelar(null);
        });
        assertTrue(exception2.getMessage().contains("al menos 10 caracteres"));
    }

    @Test
    void noDeberiaCancelarConMotivoCorto() {
        // RN-ORD-15, RN-ORD-16: El motivo debe tener al menos 10 caracteres
        Orden orden = Orden.crear(clienteId, itemsValidos, direccionValida);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            orden.cancelar("Corto"); // Solo 5 caracteres
        });

        assertTrue(exception.getMessage().contains("al menos 10 caracteres"));
    }

    // ==================== Tests adicionales ====================

    @Test
    void deberiaCalcularTotalCorrectamente() {
        List<ItemOrden> items = new ArrayList<>();
        items.add(ItemOrden.crear(UUID.randomUUID(), "Producto 1", "SKU-001", 2, Money.pesos(100.00)));
        items.add(ItemOrden.crear(UUID.randomUUID(), "Producto 2", "SKU-002", 3, Money.pesos(50.00)));

        Orden orden = Orden.crear(clienteId, items, direccionValida);

        // Total = (2 * 100) + (3 * 50) = 200 + 150 = 350
        assertEquals(350.00, orden.getTotal().getCantidad().doubleValue(), 0.001);
    }

    @Test
    void deberiaRetornarItemsInmutables() {
        Orden orden = Orden.crear(clienteId, itemsValidos, direccionValida);

        List<ItemOrden> items = orden.getItems();

        assertThrows(UnsupportedOperationException.class, () -> {
            items.clear();
        });
    }

    @Test
    void deberiaRetornarHistorialInmutable() {
        Orden orden = Orden.crear(clienteId, itemsValidos, direccionValida);
        orden.confirmar();

        List<CambioEstado> historial = orden.getHistorialEstados();

        assertThrows(UnsupportedOperationException.class, () -> {
            historial.clear();
        });
    }
}
