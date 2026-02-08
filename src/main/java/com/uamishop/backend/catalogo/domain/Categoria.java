package com.uamishop.backend.catalogo.domain;

public class Categoria {

    private final CategoriaId id;
    private String nombre;
    private String descripcion;
    private CategoriaId categoriaPadreId;

    public Categoria(CategoriaId id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public void actualizar(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public void asignarPadre(CategoriaId padreId) {
        this.categoriaPadreId = padreId;
    }

    public CategoriaId getId() {
        return id;
    }
}
