// API pública del módulo de Órdenes.
// Define el contrato explícito que este módulo expone hacia el exterior.
// Ningún caller externo debe depender de OrdenService directamente.

package com.uamishop.backend.orden.api;

import com.uamishop.backend.ventas.domain.CarritoId;
import com.uamishop.backend.orden.domain.DireccionEnvio;

import java.util.List;
import java.util.UUID;

public interface OrdenesApi {

    // ── Consultas ─────────────────────────────────────────────────────────────

    /** Devuelve el resumen público de una orden por su ID. */
    OrdenResumen obtenerOrden(UUID ordenId);

    /** Devuelve el resumen público de todas las órdenes. */
    List<OrdenResumen> listarOrdenes();

    /**
     * Devuelve la lista de filas de datos (vista ampliada) de todas las órdenes.
     */
    List<DatosResumen> listarDatos();

    // ── Comandos ──────────────────────────────────────────────────────────────

    /** Crea una orden vacía para un cliente. */
    OrdenResumen crear(UUID clienteId, DireccionEnvio direccionEnvio);

    /** Crea una orden a partir de un carrito existente. */
    OrdenResumen crearDesdeCarrito(CarritoId carritoId, DireccionEnvio direccionEnvio);

    /** Confirma una orden en estado PENDIENTE. */
    OrdenResumen confirmar(UUID ordenId);

    /** Procesa el pago de una orden CONFIRMADA. */
    OrdenResumen procesarPago(UUID ordenId, String referenciaPago);

    /** Marca una orden como en preparación (EN_PROCESO). */
    OrdenResumen marcarEnProceso(UUID ordenId);

    /** Marca una orden como enviada adjuntando el número de guía. */
    OrdenResumen marcarEnviada(UUID ordenId, String numeroGuia);

    /** Marca una orden como entregada. */
    OrdenResumen marcarEntregada(UUID ordenId);

    /** Cancela una orden indicando el motivo. */
    OrdenResumen cancelar(UUID ordenId, String motivo);
}
