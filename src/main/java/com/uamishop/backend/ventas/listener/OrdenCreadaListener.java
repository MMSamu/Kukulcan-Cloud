package com.uamishop.backend.ventas.listener;

import com.uamishop.backend.shared.event.OrdenCreadaEvent;
import com.uamishop.backend.ventas.domain.CarritoId;
import com.uamishop.backend.ventas.service.CarritoService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrdenCreadaListener {

    private final CarritoService carritoService;

    public OrdenCreadaListener(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onOrdenCreada(OrdenCreadaEvent event) {
        carritoService.completarCheckout(new CarritoId(event.carritoId()));
    }
}