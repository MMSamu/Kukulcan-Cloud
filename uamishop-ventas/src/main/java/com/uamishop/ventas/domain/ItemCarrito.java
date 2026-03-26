package com.uamishop.ventas.domain;

import com.uamishop.shared.domain.Money;
import com.uamishop.shared.domain.ProductoId;
import jakarta.persistence.*;

import java.util.UUID;

/* Representa un item en el carrito de compras */
@Entity
@Table(name = "items_carrito")
public class ItemCarrito {

    @Id
    private UUID id; // Identificador único del item (De la línea en el carrito)

    @Embedded
    @AttributeOverride(name = "valor", column = @Column(name = "producto_id"))
    private ProductoId productoId; // Referencia al producto (solo su ID, no toda la entidad)

    @Column(name = "nombre_producto")
    private String nombreProducto; // Guarda el nombre del producto para mostrar en el carrito 

    @Column(name = "sku")
    private String sku; // Guarda el SKU del producto para mostrar en el carrito

    @Column(name = "cantidad_items")
    private int cantidad; // JPA guarda el número simple

    // El precio unitario se almacena como un Money embebido, 
    // con columnas personalizadas para cantidad y moneda
    @Embedded
    @AttributeOverride(name = "cantidad", column = @Column(name = "precio_unitario_monto"))
    @AttributeOverride(name = "moneda", column = @Column(name = "precio_unitario_moneda"))    
    private Money precioUnitario; // Precio unitario del producto

    // Constructor vacio obligatorio para JPA
    protected ItemCarrito() { 
    }

    // Constructor para crear un nuevo item en el carrito
    public ItemCarrito(ProductoId productoId, int cantidad, Money precioUnitario) {
        this.id = UUID.randomUUID();
        this.productoId = productoId;
        // Validamos usando Record, pero guardamos el valor primitivo
        this.cantidad = new Cantidad(cantidad).valor();
        this.precioUnitario = precioUnitario;
    }

    // Aumenta la cantidad sumando a la existente (ej: agregar más del mismo producto)
    public void aumentarCantidad(Cantidad cantidadExtra) {
        Cantidad actual = new Cantidad(this.cantidad);
        this.cantidad = actual.sumar(cantidadExtra).valor();
    }

    // Actualiza la cantidad a un nuevo valor (ej: modificar desde el carrito)
    public void actualizarCantidad(Cantidad nuevaCantidad) {
        this.cantidad = nuevaCantidad.valor();
    }

    // Calcula el subtotal multiplicando el precio unitario por la cantidad
    public Money subtotal() {
        return precioUnitario.multiplicar(cantidad);
    }

    // Getters
    public ProductoId getProductoId() { 
        return productoId; 
    }

    public String getNombreProducto() { 
        return nombreProducto; 
    }

    public String getSku() { 
        return sku; 
    }                       

    public int getCantidad() { 
        return cantidad; 
    }
    
    public Money getPrecioUnitario() { 
        return precioUnitario; 
    }
}