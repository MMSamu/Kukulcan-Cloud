package com.uamishop.backend.orden.service;

import com.uamishop.backend.shared.domain.Money;
import com.uamishop.backend.shared.exception.DomainException;
import com.uamishop.backend.ventas.domain.Carrito;
//import com.uamishop.backend.ventas.domain.CarritoId;
import com.uamishop.backend.orden.domain.DireccionEnvio;
import com.uamishop.backend.orden.domain.Orden;
import com.uamishop.backend.orden.domain.OrdenId;
import com.uamishop.backend.orden.repository.OrdenJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;

@Service
public class OrdenService {

    private final OrdenJpaRepository ordenRepository;

    public OrdenService(OrdenJpaRepository ordenRepository) {
        this.ordenRepository = ordenRepository;
    }

    @Transactional
    public Orden crear(UUID clienteId) {
        Orden orden = new Orden(clienteId);
        return ordenRepository.save(orden);
    }

    /*
     * @Transactional
     * public Orden crearDesdeCarrito(CarritoId carritoId, DireccionEnvio
     * direccionEnvio) {
     * 
     * }
     */

    @Transactional(readOnly = true)
    public Orden buscarPorId(UUID clienteId) {
        return ordenRepository.findById(clienteId).orElseThrow(() -> new DomainException("Orden no encontrada"));
    }

    @Transactional
    public List<Orden> buscarTodas() {
        return ordenRepository.findAll();
    }

    @Transactional
    public Orden confirmar(UUID ordenId) {
        Orden orden = buscarPorId(ordenId);
        orden.confirmar();
        return ordenRepository.save(orden);
    }

    @Transactional
    public Orden procesarPago(UUID ordenId, String referenciaPago) {
        Orden orden = buscarPorId(ordenId);
        orden.procesarPago(referenciaPago);
        return ordenRepository.save(orden);
    }

    @Transactional
    public Orden marcarEnviada(UUID ordenId, String numeroGuia) {
        Orden orden = buscarPorId(ordenId);
        orden.marcarEnviada(numeroGuia);
        return ordenRepository.save(orden);
    }

    @Transactional
    public Orden marcarEnProceso(UUID ordenId) {
        Orden orden = buscarPorId(ordenId);
        orden.marcarEnProceso();
        return ordenRepository.save(orden);
    }

    @Transactional
    public Orden marcarEntregada(UUID ordenId) {
        Orden orden = buscarPorId(ordenId);
        orden.marcarEntregada();
        return ordenRepository.save(orden);
    }

    /*
     * @Transactional
     * public Orden entregar(UUID ordenId) {
     * Orden orden = buscarPorId(ordenId);
     * orden.entregar();
     * return ordenRepository.save(orden);
     * }
     */

    @Transactional
    public Orden cancelar(UUID ordenId, String motivo) {
        Orden orden = buscarPorId(ordenId);
        orden.cancelar(motivo);
        return ordenRepository.save(orden);
    }
}
