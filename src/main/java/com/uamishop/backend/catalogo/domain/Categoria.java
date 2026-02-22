package com.uamishop.backend.catalogo.domain;

public class Categoria {

    private final CategoriaId id;
    private String nombre;
    private String descripcion;
    private CategoriaId categoriaPadreId;

    public Categoria(CategoriaId id, String nombre, String descripcion) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public void actualizar(String nombre, String descripcion) {
        if(nombre == null || nombre.isBlank()){
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }
        /**this.nombre = nombre;
        this.descripcion = descripcion;*/
    }

    public void asignarPadre(CategoriaId padreId) {

        this.categoriaPadreId = padreId;
    }

    public CategoriaId getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public CategoriaId getCategoriaPadreId() {
        return categoriaPadreId;
    }
}
