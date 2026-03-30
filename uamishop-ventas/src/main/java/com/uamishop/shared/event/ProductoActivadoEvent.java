package com.uamishop.shared.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoActivadoEvent {
    private UUID eventoId;
    private Instant ocurridoEn;
    
    private UUID productoId;
    private String nombre;
    private String sku;
    private BigDecimal precioMonto;
    private String precioMoneda;
}