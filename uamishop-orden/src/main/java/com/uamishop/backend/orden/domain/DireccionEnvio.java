package com.uamishop.backend.orden.domain;

import jakarta.persistence.Embeddable;
import java.util.Objects;

/**
 * Value Object que representa una dirección de envío válida.
 * Valida reglas de negocio relacionadas con direcciones para órdenes.
 */
@Embeddable
public class DireccionEnvio {
    private String calle;
    private String ciudad;
    private String estado;
    private String codigoPostal;
    private String pais;
    private String telefonoContacto;

    // Constructor sin argumentos requerido por JPA
    protected DireccionEnvio() {
    }

    private DireccionEnvio(String calle, String ciudad, String estado, String codigoPostal,
            String pais, String telefonoContacto) {
        // RN-VO-04: El país debe ser "México"
        if (!"México".equals(pais)) {
            throw new IllegalArgumentException("Solo se permiten envíos a México");
        }

        // RN-ORD-03: El código postal debe ser de 5 dígitos
        if (codigoPostal == null || !codigoPostal.matches("\\d{5}")) {
            throw new IllegalArgumentException("El código postal debe ser de 5 dígitos");
        }

        // RN-ORD-04: El teléfono de contacto debe ser de 10 dígitos
        if (telefonoContacto == null || !telefonoContacto.matches("\\d{10}")) {
            throw new IllegalArgumentException("El teléfono de contacto debe ser de 10 dígitos");
        }

        if (calle == null || calle.trim().isEmpty()) {
            throw new IllegalArgumentException("La calle no puede estar vacía");
        }

        if (ciudad == null || ciudad.trim().isEmpty()) {
            throw new IllegalArgumentException("La ciudad no puede estar vacía");
        }

        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado no puede estar vacío");
        }

        this.calle = calle;
        this.ciudad = ciudad;
        this.estado = estado;
        this.codigoPostal = codigoPostal;
        this.pais = pais;
        this.telefonoContacto = telefonoContacto;
    }

    public static DireccionEnvio crear(String calle, String ciudad, String estado,
            String codigoPostal, String telefonoContacto) {
        return new DireccionEnvio(calle, ciudad, estado, codigoPostal, "México", telefonoContacto);
    }

    public boolean esValido() {
        return calle != null && !calle.trim().isEmpty() &&
                ciudad != null && !ciudad.trim().isEmpty() &&
                estado != null && !estado.trim().isEmpty() &&
                codigoPostal != null && codigoPostal.matches("\\d{5}") &&
                pais != null && pais.equals("México") &&
                telefonoContacto != null && telefonoContacto.matches("\\d{10}");
    }

    public String getCalle() {
        return calle;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getEstado() {
        return estado;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public String getPais() {
        return pais;
    }

    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DireccionEnvio that = (DireccionEnvio) o;
        return Objects.equals(calle, that.calle) &&
                Objects.equals(ciudad, that.ciudad) &&
                Objects.equals(estado, that.estado) &&
                Objects.equals(codigoPostal, that.codigoPostal) &&
                Objects.equals(pais, that.pais) &&
                Objects.equals(telefonoContacto, that.telefonoContacto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(calle, ciudad, estado, codigoPostal, pais, telefonoContacto);
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s, CP: %s, %s", calle, ciudad, estado, codigoPostal, pais);
    }
}
