package com.uamishop.backend.orden.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para DireccionEnvio Value Object.
 * Valida RN-VO-04 (país México), RN-ORD-03 (CP 5 dígitos), RN-ORD-04 (teléfono
 * 10 dígitos).
 */
class DireccionEnvioTest {

    @Test
    void deberiaCrearDireccionValida() {
        DireccionEnvio direccion = DireccionEnvio.crear(
                "Av. Insurgentes Sur 123",
                "Ciudad de México",
                "CDMX",
                "01234",
                "5512345678");

        assertNotNull(direccion);
        assertEquals("México", direccion.getPais());
        assertEquals("01234", direccion.getCodigoPostal());
        assertEquals("5512345678", direccion.getTelefonoContacto());
    }

    @Test
    void deberiaValidarPaisEsMexico() {
        // RN-VO-04: Solo se permiten envíos a México
        // El factory method siempre establece "México" como país
        DireccionEnvio direccion = DireccionEnvio.crear(
                "Calle Principal 456",
                "Guadalajara",
                "Jalisco",
                "44100",
                "3312345678");

        assertEquals("México", direccion.getPais());
    }

    @Test
    void noDeberiaPermitirCodigoPostalInvalido() {
        // RN-ORD-03: El código postal debe ser de 5 dígitos

        // CP con menos de 5 dígitos
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            DireccionEnvio.crear(
                    "Av. Universidad 789",
                    "Monterrey",
                    "Nuevo León",
                    "1234", // Solo 4 dígitos
                    "8112345678");
        });
        assertTrue(exception1.getMessage().contains("5 dígitos"));

        // CP con más de 5 dígitos
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            DireccionEnvio.crear(
                    "Av. Universidad 789",
                    "Monterrey",
                    "Nuevo León",
                    "123456", // 6 dígitos
                    "8112345678");
        });
        assertTrue(exception2.getMessage().contains("5 dígitos"));

        // CP con letras
        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> {
            DireccionEnvio.crear(
                    "Av. Universidad 789",
                    "Monterrey",
                    "Nuevo León",
                    "ABC12",
                    "8112345678");
        });
        assertTrue(exception3.getMessage().contains("5 dígitos"));
    }

    @Test
    void noDeberiaPermitirTelefonoInvalido() {
        // RN-ORD-04: El teléfono debe ser de 10 dígitos

        // Teléfono con menos de 10 dígitos
        Exception exception1 = assertThrows(IllegalArgumentException.class, () -> {
            DireccionEnvio.crear(
                    "Calle Reforma 321",
                    "Puebla",
                    "Puebla",
                    "72000",
                    "221234567" // Solo 9 dígitos
            );
        });
        assertTrue(exception1.getMessage().contains("10 dígitos"));

        // Teléfono con más de 10 dígitos
        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            DireccionEnvio.crear(
                    "Calle Reforma 321",
                    "Puebla",
                    "Puebla",
                    "72000",
                    "22123456789" // 11 dígitos
            );
        });
        assertTrue(exception2.getMessage().contains("10 dígitos"));

        // Teléfono con letras
        Exception exception3 = assertThrows(IllegalArgumentException.class, () -> {
            DireccionEnvio.crear(
                    "Calle Reforma 321",
                    "Puebla",
                    "Puebla",
                    "72000",
                    "221234567A");
        });
        assertTrue(exception3.getMessage().contains("10 dígitos"));
    }

    @Test
    void noDeberiaPermitirCalleVacia() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DireccionEnvio.crear(
                    "",
                    "Ciudad",
                    "Estado",
                    "12345",
                    "5512345678");
        });
        assertTrue(exception.getMessage().contains("calle"));
    }

    @Test
    void noDeberiaPermitirCiudadVacia() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DireccionEnvio.crear(
                    "Calle 123",
                    "",
                    "Estado",
                    "12345",
                    "5512345678");
        });
        assertTrue(exception.getMessage().contains("ciudad"));
    }

    @Test
    void noDeberiaPermitirEstadoVacio() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            DireccionEnvio.crear(
                    "Calle 123",
                    "Ciudad",
                    "",
                    "12345",
                    "5512345678");
        });
        assertTrue(exception.getMessage().contains("estado"));
    }
}
