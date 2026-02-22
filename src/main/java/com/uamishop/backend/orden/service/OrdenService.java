package com.uamishop.backend.orden.service;

//import com.uamishop.backend.shared.domain.Money;
import com.uamishop.backend.shared.exception.DomainException;
import com.uamishop.backend.ventas.domain.Carrito;
import com.uamishop.backend.ventas.domain.CarritoId;
import com.uamishop.backend.ventas.repository.CarritoJpaRepository;
import com.uamishop.backend.orden.domain.DireccionEnvio;
import com.uamishop.backend.orden.domain.Orden;
import com.uamishop.backend.orden.repository.OrdenJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.List;

@Service
public class OrdenService {

    // Inyección de dependencias
    private final OrdenJpaRepository ordenRepository;
    // Repositorio para acceder a los datos del carrito
    private final CarritoJpaRepository carritoRepository;

    // Constructor para la inyección de dependencias
    public OrdenService(OrdenJpaRepository ordenRepository, CarritoJpaRepository carritoRepository) {
        // Inyecta el repositorio de órdenes
        this.ordenRepository = ordenRepository;
        // Inyecta el repositorio de carritos
        this.carritoRepository = carritoRepository;
    }

    // Crea una nueva orden
    @Transactional
    public Orden crear(UUID clienteId, DireccionEnvio direccionEnvio) {
        // Crea una nueva orden
        Orden orden = new Orden(clienteId, direccionEnvio);
        // Guarda la orden
        return ordenRepository.save(orden);
    }

    // Crea una orden desde un carrito
    @Transactional
    public Orden crearDesdeCarrito(CarritoId carritoId, DireccionEnvio direccionEnvio) {
        // 1. Buscar el carrito usando su UUID, no el wrapper CarritoId
        Carrito carrito = carritoRepository.findById(carritoId.value())
                .orElseThrow(() -> new DomainException("Carrito no encontrado"));

        // 2. Construir la Orden con los datos del Carrito
        Orden orden = new Orden(carrito.getClienteId(), direccionEnvio);

        for (com.uamishop.backend.ventas.domain.ItemCarrito itemCarrito : carrito.getItems()) {
            com.uamishop.backend.orden.domain.ItemOrden itemOrden = com.uamishop.backend.orden.domain.ItemOrden.crear(
                    itemCarrito.getProductoId(),
                    itemCarrito.getNombreProducto(),
                    itemCarrito.getSku(),
                    itemCarrito.getCantidad(),
                    itemCarrito.getPrecioUnitario());
            orden.agregarItem(itemOrden);
        }

        // 3. Aplicar descuento si existía en el carrito
        if (carrito.getDescuento() != null && carrito.getDescuento().esPositivo()) {
            orden.aplicarDescuento(carrito.getDescuento());
        }

        // 4. Marcar el carrito como completado
        carrito.completarCheckout();
        carritoRepository.save(carrito);

        return ordenRepository.save(orden);
    }

    // Busca una orden por su ID
    @Transactional(readOnly = true)
    public Orden buscarPorId(UUID clienteId) {
        // Devuelve la orden encontrada o lanza una excepción si no existe
        return ordenRepository.findById(clienteId).orElseThrow(() -> new DomainException("Orden no encontrada"));
    }

    // Busca todas las órdenes
    @Transactional
    public List<Orden> buscarTodas() {
        // Devuelve todas las órdenes
        return ordenRepository.findAll();
    }

    // Confirma una orden
    @Transactional
    public Orden confirmar(UUID ordenId) {
        // Busca la orden por su ID
        Orden orden = buscarPorId(ordenId);
        // Confirma la orden
        orden.confirmar();
        // Guarda la orden
        return ordenRepository.save(orden);
    }

    // Procesa el pago de una orden
    @Transactional
    public Orden procesarPago(UUID ordenId, String referenciaPago) {
        // Busca la orden por su ID
        Orden orden = buscarPorId(ordenId);
        // Procesa el pago de la orden
        orden.procesarPago(referenciaPago);
        // Guarda la orden
        return ordenRepository.save(orden);
    }

    // Marca una orden como enviada
    @Transactional
    public Orden marcarEnviada(UUID ordenId, String numeroGuia) {
        // Busca la orden por su ID
        Orden orden = buscarPorId(ordenId);
        // Marca la orden como enviada
        orden.marcarEnviada(numeroGuia);
        // Guarda la orden
        return ordenRepository.save(orden);
    }

    // Marca una orden como en proceso
    @Transactional
    public Orden marcarEnProceso(UUID ordenId) {
        // Busca la orden por su ID
        Orden orden = buscarPorId(ordenId);
        // Marca la orden como en proceso
        orden.marcarEnProceso();
        // Guarda la orden
        return ordenRepository.save(orden);
    }

    // Marca una orden como entregada
    @Transactional
    public Orden marcarEntregada(UUID ordenId) {
        // Busca la orden por su ID
        Orden orden = buscarPorId(ordenId);
        // Marca la orden como entregada
        orden.marcarEntregada();
        // Guarda la orden
        return ordenRepository.save(orden);
    }

    @Transactional
    public Orden cancelar(UUID ordenId, String motivo) {
        // Busca la orden por su ID
        Orden orden = buscarPorId(ordenId);
        // Cancela la orden
        orden.cancelar(motivo);
        // Guarda la orden
        return ordenRepository.save(orden);
    }
}
