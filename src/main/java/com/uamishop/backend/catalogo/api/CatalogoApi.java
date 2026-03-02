package com.uamishop.backend.catalogo.api;

import java.util.UUID;
import java.util.List;

public interface CatalogoApi {

    ProductoResumen obtenerProducto(UUID productoId);

    List<ProductoResumen> listarProductos();

    List<ProductoResumen> listarPorCategoria(UUID categoriaId);

}
