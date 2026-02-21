package com.uamishop.backend.catalogo.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "categorias")
public class CategoriaEntity {

    @Id
    private UUID id;

    private String nombre;
    private String descripcion;

    private UUID categoriaPadreId;

    // Constructor vacío requerido por JPA
    protected CategoriaEntity() {
    }

    public CategoriaEntity(UUID id, String nombre, String descripcion, UUID categoriaPadreId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoriaPadreId = categoriaPadreId;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public UUID getCategoriaPadreId() {
        return categoriaPadreId;
    }
}
