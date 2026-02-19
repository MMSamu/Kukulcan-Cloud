package com.uamishop.backend.catalogo.infrastructure.persistence;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "productos")
public class ProductoEntity {

    @Id
    private UUID id;

    private String nombre;

    private String descripcion;

    private BigDecimal precio;

    private UUID categoriaId;

    private boolean disponible;

    private LocalDateTime fechaCreacion;

    protected ProductoEntity() {}

    public ProductoEntity(UUID id,
                          String nombre,
                          String descripcion,
                          BigDecimal precio,
                          UUID categoriaId,
                          boolean disponible,
                          LocalDateTime fechaCreacion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoriaId = categoriaId;
        this.disponible = disponible;
        this.fechaCreacion = fechaCreacion;
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public BigDecimal getPrecio() { return precio; }
    public UUID getCategoriaId() { return categoriaId; }
    public boolean isDisponible() { return disponible; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}