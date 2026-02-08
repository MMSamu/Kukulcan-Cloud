package com.uamishop.backend.catalogo.domain;

import java.util.UUID;

public class CategoriaId {

    private final UUID valor;

    private CategoriaId(UUID valor){
        this.valor = valor;
    }

    public static CategoriaId generar(){
        return new CategoriaId(UUID.randomUUID());
    }

    public UUID getValue(){
        return valor;
    }
}
