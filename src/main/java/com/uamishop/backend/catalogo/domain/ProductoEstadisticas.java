package com.uamishop.backend.catalogo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Se agrega entidad que alamcena estadisticas acumuladas de un producto.
 * Estas estadisticas se actualizan a partir de eventos del sistema.
 * como compras o cuando se agrega un producto al carrito.
 */

@Entity
@Table(name = "producto_estadisticas")
public class ProductoEstadisticas {

    /**
     * ID del prodcuto al que pertenecen las estadisticas
     * Se guarda como VARBINARY(16) para alamacenar el UUID de forma eficiente
     *
     */

    @Id
    @Column(columnDefinition = "VARBINARY(16)")
    private UUID productoId;

    //numero total de transacciones en las que se ha vendido el producto
    private long ventasTotales;

    //Cantidad total de unidades vendidas del producto.
    private long cantidadVendida;

    //numero de veces que el producto fue agregado al carrito
    private long vecesAgregadoAlCarrito;

    //fecha de la ultima vez que el producto fue vendido
    private Instant ultimaVentaAt;

    //fecha de la ultima vez que el producto fue agregado al carrito
    private Instant ultimaAgregadoAlCarritoAt;

    //Constructor vacio requerico por JPA, tiene que estar vacio.
    public  ProductoEstadisticas(){

    }

    //Constructor para inicializar estadisticas para un producto.
    public ProductoEstadisticas(UUID productoId){
        this.productoId = productoId;
        this.ventasTotales = 0;
        this.cantidadVendida = 0;
        this.vecesAgregadoAlCarrito = 0;
    }


    //Getter y Setters

    public UUID getProductoId() {
        return productoId;
    }

    public void setProductoId(UUID productoId){
        this.productoId = productoId;
    }

    public long getVentasTotales() {
        return ventasTotales;
    }

    public void setVentasTotales(long ventasTotales) {
        this.ventasTotales = ventasTotales;
    }

    public long getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(long cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public long getVecesAgregadoAlCarrito() {
        return vecesAgregadoAlCarrito;
    }

    public void setVecesAgregadoAlCarrito(long vecesAgregadoAlCarrito) {
        this.vecesAgregadoAlCarrito = vecesAgregadoAlCarrito;
    }

    public Instant getUltimaVentaAt() {
        return ultimaVentaAt;
    }

    public void setUltimaVentaAt(Instant ultimaVentaAt) {
        this.ultimaVentaAt = ultimaVentaAt;
    }

    public Instant getUltimaAgregadoAlCarritoAt() {
        return ultimaAgregadoAlCarritoAt;
    }

    public void setUltimaAgregadoAlCarritoAt(Instant ultimaAgregadoAlCarritoAt) {
        this.ultimaAgregadoAlCarritoAt = ultimaAgregadoAlCarritoAt;
    }

}
