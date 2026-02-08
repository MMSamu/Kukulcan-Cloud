package com.uamishop.backend.catalogo.domain;

import java.util.UUID;

//Value Object
public class ProductoId {

    private final UUID valor;

   private ProductoId(UUID valor){
       this.valor = valor;
   }

   public static ProductoId generar(){
       return new ProductoId(UUID.randomUUID());
   }

   public UUID getValue() {
       return valor;
   }


}
