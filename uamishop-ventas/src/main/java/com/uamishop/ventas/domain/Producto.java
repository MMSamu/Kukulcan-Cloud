package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.ProductoId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "productos")
@Getter
@NoArgsConstructor
public class Producto {
    @Id
    private UUID id;

    private String nombre;
    private String sku;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "cantidad", column = @Column(name = "precio_monto")),
        @AttributeOverride(name = "moneda", column = @Column(name = "precio_moneda"))
    })
    private Money precio;

    public Producto(UUID id, String nombre, String sku, Money precio) {
        this.id = id;
        this.nombre = nombre;
        this.sku = sku;
        this.precio = precio;
    }
}