package com.uamishop.orden.service;

import com.uamishop.shared.exception.DomainException;
import com.uamishop.catalogo.api.CatalogoApi;
import com.uamishop.catalogo.api.ProductoResumen;
import com.uamishop.orden.config.RabbitConfig;
import com.uamishop.orden.controller.dto.ItemOrdenRequest;
import com.uamishop.orden.controller.dto.OrdenResumen;
import com.uamishop.orden.domain.DireccionEnvio;
import com.uamishop.orden.domain.ItemOrden;
import com.uamishop.orden.domain.Orden;
import com.uamishop.shared.domain.Money;
import com.uamishop.orden.repository.OrdenJpaRepository;
import com.uamishop.shared.event.OrdenCreadaEvent;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class OrdenService {

    private final OrdenJpaRepository ordenRepository;
    private final OutboxService outboxService;
    private final CatalogoApi catalogoApi;

    public OrdenService(OrdenJpaRepository ordenRepository,
            OutboxService outboxService,
            CatalogoApi catalogoApi) {
        this.ordenRepository = ordenRepository;
        this.outboxService = outboxService;
        this.catalogoApi = catalogoApi;
    }

    @Transactional
    public OrdenResumen registrarOActualizarMonto(UUID ordenId, UUID clienteId, DireccionEnvio direccionEnvio,
            BigDecimal monto, List<ItemOrdenRequest> items) {
        Orden orden;

        if (ordenId == null) {
            orden = new Orden(clienteId, direccionEnvio);
        } else {
            orden = ordenRepository.findById(ordenId)
                    .map(existente -> {
                        if (direccionEnvio != null) {
                            setPrivateField(existente, "direccionEnvio", direccionEnvio);
                        }
                        return existente;
                    })
                    .orElseGet(() -> {
                        System.out.println("Creando orden nueva con ID de carrito: " + ordenId);
                        Orden nueva = crearOrdenConIdEspecifico(ordenId, clienteId, direccionEnvio);
                        if (items != null && !items.isEmpty()) {
                            cargarItemsEnOrden(nueva, items);
                        }
                        return nueva;
                    });
        }

        // Manejo de montos mediante Reflexión para la clase Money
        if (monto != null) {
            Money moneyMonto = Money.pesos(monto.doubleValue());
            setPrivateField(orden, "total", moneyMonto);
            setPrivateField(orden, "subtotal", moneyMonto);
        } else {
            Money totalActual = (Money) getPrivateField(orden, "total");
            BigDecimal valorTotal = (totalActual != null) ? totalActual.getCantidad() : BigDecimal.ZERO;

            if (valorTotal == null || valorTotal.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal totalCalculado = calcularTotalDesdeCatalogoUsandoReflexion(orden);
                Money moneyCalculado = Money.pesos(totalCalculado.doubleValue());
                setPrivateField(orden, "subtotal", moneyCalculado);
                setPrivateField(orden, "total", moneyCalculado);
            }
        }

        Orden guardada = ordenRepository.save(orden);
        notificarOrdenCreada(guardada, clienteId);
        return OrdenResumen.desde(guardada);
    }

    @Transactional
    public OrdenResumen crear(UUID clienteId, DireccionEnvio direccionEnvio, List<ItemOrdenRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new DomainException("No se puede crear una orden sin productos.");
        }
        Orden orden = new Orden(clienteId, direccionEnvio);
        BigDecimal totalCalculado = BigDecimal.ZERO;
        for (ItemOrdenRequest item : items) {
            ProductoResumen producto = catalogoApi.obtenerProducto(item.productoId());
            if (producto != null) {
                BigDecimal precioUnitario = producto.precio().getCantidad();
                totalCalculado = totalCalculado.add(precioUnitario.multiply(BigDecimal.valueOf(item.cantidad())));
            }
        }

        Money moneyTotal = Money.pesos(totalCalculado.doubleValue());
        setPrivateField(orden, "subtotal", moneyTotal);
        setPrivateField(orden, "total", moneyTotal);

        Orden guardada = ordenRepository.save(orden);
        notificarOrdenCreada(guardada, clienteId);
        return OrdenResumen.desde(guardada);
    }

    @Transactional(readOnly = true)
    public OrdenResumen obtenerOrden(UUID ordenId) {
        return OrdenResumen.desde(buscarPorId(ordenId));
    }

    @Transactional(readOnly = true)
    public List<OrdenResumen> listarOrdenes() {
        return ordenRepository.findAll().stream().map(OrdenResumen::desde).toList();
    }

    @Transactional
    public OrdenResumen confirmar(UUID ordenId) {
        Orden orden = buscarPorId(ordenId);
        orden.confirmar();
        return OrdenResumen.desde(ordenRepository.save(orden));
    }

    @Transactional
    public OrdenResumen procesarPago(UUID ordenId, String referenciaPago) {
        Orden orden = buscarPorId(ordenId);
        orden.confirmar();
        orden.procesarPago(referenciaPago);
        return OrdenResumen.desde(ordenRepository.save(orden));
    }

    @Transactional
    public OrdenResumen marcarEnProceso(UUID ordenId) {
        Orden orden = buscarPorId(ordenId);
        orden.marcarEnProceso();
        return OrdenResumen.desde(ordenRepository.save(orden));
    }

    @Transactional
    public OrdenResumen marcarEnviada(UUID ordenId) {
        Orden orden = buscarPorId(ordenId);
        DireccionEnvio destino = orden.getDireccionEnvio();
        if (destino == null || destino.getEstado() == null) {
            throw new DomainException("La orden no tiene una dirección de envío válida.");
        }
        String prefijo = destino.getEstado().length() >= 3 ? destino.getEstado().substring(0, 3).toUpperCase() : "GEN";
        String guia = "ENV-" + prefijo + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        orden.marcarEnviada(guia);
        return OrdenResumen.desde(ordenRepository.save(orden));
    }

    @Transactional
    public OrdenResumen marcarEntregada(UUID ordenId) {
        Orden orden = buscarPorId(ordenId);
        orden.marcarEntregada();
        return OrdenResumen.desde(ordenRepository.save(orden));
    }

    @Transactional
    public OrdenResumen cancelar(UUID ordenId, String motivo) {
        Orden orden = buscarPorId(ordenId);
        orden.cancelar(motivo);
        return OrdenResumen.desde(ordenRepository.save(orden));
    }

    private Orden buscarPorId(UUID id) {
        return ordenRepository.findById(id).orElseThrow(() -> new DomainException("Orden no encontrada: " + id));
    }

    private void notificarOrdenCreada(Orden guardada, UUID clienteId) {
        UUID idOrden = guardada.getId().valor();
        // El id de la orden y el del carrito son el mismo en este flujo
        OrdenCreadaEvent evento = new OrdenCreadaEvent(
                UUID.randomUUID(),
                Instant.now(),
                idOrden,
                idOrden,
                clienteId);
        outboxService.guardarEvento(RabbitConfig.EVENTS_EXCHANGE, RabbitConfig.RK_ORDEN_CREADA, evento);
    }

    private BigDecimal calcularTotalDesdeCatalogoUsandoReflexion(Orden orden) {
        try {
            List<ItemOrden> items = (List<ItemOrden>) getPrivateField(orden, "items");
            if (items == null || items.isEmpty())
                return BigDecimal.ZERO;

            BigDecimal total = BigDecimal.ZERO;
            for (ItemOrden item : items) {
                ProductoResumen producto = catalogoApi.obtenerProducto(item.getProductoId());
                if (producto != null) {
                    BigDecimal precio = producto.precio().getCantidad();
                    total = total.add(precio.multiply(BigDecimal.valueOf(item.getCantidad())));
                }
            }
            return total;
        } catch (Exception e) {
            throw new RuntimeException("Error al calcular total", e);
        }
    }

    private void cargarItemsEnOrden(Orden orden, List<ItemOrdenRequest> itemsRequest) {
        try {
            List<ItemOrden> listaItems = (List<ItemOrden>) getPrivateField(orden, "items");
            for (ItemOrdenRequest req : itemsRequest) {
                ItemOrden nuevoItem = ItemOrden.crear(
                        req.productoId(),
                        "Pendiente",
                        "SKU",
                        req.cantidad(),
                        Money.pesos(1.0));
                listaItems.add(nuevoItem);
            }
        } catch (Exception e) {
            System.out.println("Error cargando ítems: " + e.getMessage());
        }
    }

    private Orden crearOrdenConIdEspecifico(UUID ordenId, UUID clienteId, DireccionEnvio direccionEnvio) {
        Orden nuevaOrden = new Orden(clienteId, direccionEnvio);
        setPrivateField(nuevaOrden, "id", ordenId);
        return nuevaOrden;
    }

    // --- MÉTODOS DE APOYO PARA REFLEXIÓN ---

    private void setPrivateField(Object object, String fieldName, Object value) {
        try {
            Field field = Orden.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            System.out.println("Error seteando campo " + fieldName + ": " + e.getMessage());
        }
    }

    private Object getPrivateField(Object object, String fieldName) {
        try {
            Field field = Orden.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            return null;
        }
    }
}