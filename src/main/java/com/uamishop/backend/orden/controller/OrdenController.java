package com.uamishop.backend.orden.controller;

import com.uamishop.backend.shared.domain.Money;
import com.uamishop.backend.orden.domain.*;
import com.uamishop.backend.orden.service.OrdenService;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/ordenes")

public class OrdenController {

    private final OrdenService service;

    public OrdenController(OrdenService service) {
        this.service = service;
    }

    @PostMapping
    public Orden crear(UUID clienteId) {
        return service.crear(clienteId);
    }

    /*
     * @PostMapping("/{id}/orden")
     * public Orden crearDesdeCarrito(@PathVariable UUID id, String direccionEnvio)
     * {
     * return service.crearDesdeCarrito(new OrdenId(id), direccionEnvio);
     * }
     */

    @GetMapping("/{id}")
    public Orden buscarPorId(@PathVariable UUID clienteId) {
        return service.buscarPorId(clienteId);
    }

    @GetMapping
    public List<Orden> buscarTodas() {
        return service.buscarTodas();
    }

    @PostMapping("/{clienteId}/confirmar")
    public Orden confirmar(@PathVariable UUID clienteId) {
        return service.confirmar(clienteId);
    }

    @PostMapping("/{clienteId}/procesar-pago")
    public Orden procesarPago(@PathVariable UUID clienteId, String referenciaPago) {
        return service.procesarPago(clienteId, referenciaPago);
    }

    @PostMapping("/{clienteId}/marcar-en-proceso")
    public Orden marcarEnProceso(@PathVariable UUID clienteId) {
        return service.marcarEnProceso(clienteId);
    }

    @PostMapping("/{clienteId}/marcar-enviada")
    public Orden marcarEnviada(@PathVariable UUID ordenID, String numeroGuia) {
        return service.marcarEnviada(ordenID, numeroGuia);
    }

    @PostMapping("/{clienteId}/marcar-entregada")
    public Orden marcarEntregada(@PathVariable UUID clienteId) {
        return service.marcarEntregada(clienteId);
    }

    @PostMapping("/{clienteId}/cancelar")
    public Orden cancelar(@PathVariable UUID clienteId, String motivo) {
        return service.cancelar(clienteId, motivo);
    }

}
