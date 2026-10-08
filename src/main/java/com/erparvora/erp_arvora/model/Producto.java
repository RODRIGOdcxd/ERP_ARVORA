package com.erparvora.erp_arvora.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //Column nullable = false significa que el campo no puede ser null
    @Column(nullable = false, unique = true)
    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @Column(nullable = false)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "marca_id")
    private Marca marca;

    private String modelo;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Column(nullable = false, precision = 12, scale = 2)
    @NotNull(message = "El precio de compra es obligatorio")
    @DecimalMin(value = "0.00", message = "El precio de compra no puede ser negativo")
    private BigDecimal precioCompra;

    @Column(nullable = false, precision = 12, scale = 2)
    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.00", message = "El precio de venta no puede ser negativo")
    private BigDecimal precioVenta;

    @Column(nullable = false)
    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;

    @Column(name = "stock_minimo")
    @PositiveOrZero(message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    @ManyToOne
    @JoinColumn(name = "unidad_medida_id")
    private UnidadMedida unidadMedida;

    @Column(name = "stock_reservado")
    @PositiveOrZero(message = "El stock reservado no puede ser negativo")
    private Integer stockReservado = 0;

    @Column(name = "stock_disponible")
    @PositiveOrZero(message = "El stock disponible no puede ser negativo")
    private Integer stockDisponible;

    @OneToOne
    @JoinColumn(name = "imagen_producto_id")
    private ImagenProducto imagenProducto;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")   
    private LocalDateTime fechaActualizacion;

    public Producto() {
    }

    public Producto(String codigo, String nombre, String descripcion, Marca marca,
                    String modelo, Categoria categoria, BigDecimal precioCompra,
                    BigDecimal precioVenta, Integer stock, Integer stockMinimo,
                    UnidadMedida unidadMedida, ImagenProducto imagenProducto, Boolean activo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.marca = marca;
        this.modelo = modelo;
        this.categoria = categoria;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.unidadMedida = unidadMedida;
        this.imagenProducto = imagenProducto;
        this.activo = activo;
        this.stockReservado = 0;
        this.stockDisponible = stock != null ? stock : 0;
    }

    public static ProductoBuilder builder() {
        return new ProductoBuilder();
    }

    public static class ProductoBuilder {
        private String codigo;
        private String nombre;
        private String descripcion;
        private Marca marca;
        private String modelo;
        private Categoria categoria;
        private BigDecimal precioCompra;
        private BigDecimal precioVenta;
        private Integer stock;
        private Integer stockMinimo;
        private UnidadMedida unidadMedida;
        private ImagenProducto imagenProducto;
        private Boolean activo = true;

        public ProductoBuilder codigo(String codigo) {
            this.codigo = codigo;
            return this;
        }

        public ProductoBuilder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }

        public ProductoBuilder descripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }

        public ProductoBuilder marca(Marca marca) {
            this.marca = marca;
            return this;
        }

        public ProductoBuilder modelo(String modelo) {
            this.modelo = modelo;
            return this;
        }

        public ProductoBuilder categoria(Categoria categoria) {
            this.categoria = categoria;
            return this;
        }

        public ProductoBuilder precioCompra(BigDecimal precioCompra) {
            this.precioCompra = precioCompra;
            return this;
        }

        public ProductoBuilder precioVenta(BigDecimal precioVenta) {
            this.precioVenta = precioVenta;
            return this;
        }

        public ProductoBuilder stock(Integer stock) {
            this.stock = stock;
            return this;
        }

        public ProductoBuilder stockMinimo(Integer stockMinimo) {
            this.stockMinimo = stockMinimo;
            return this;
        }

        public ProductoBuilder unidadMedida(UnidadMedida unidadMedida) {
            this.unidadMedida = unidadMedida;
            return this;
        }

        public ProductoBuilder imagenProducto(ImagenProducto imagenProducto) {
            this.imagenProducto = imagenProducto;
            return this;
        }

        public ProductoBuilder activo(Boolean activo) {
            this.activo = activo;
            return this;
        }

        public Producto build() {
            Producto producto = new Producto(codigo, nombre, descripcion, marca, modelo,
                    categoria, precioCompra, precioVenta, stock, stockMinimo,
                    unidadMedida, imagenProducto, activo);
            producto.calcularStockDisponible();
            return producto;
        }
    }

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.activo == null) {
            this.activo = true;
        }
        calcularStockDisponible();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
        calcularStockDisponible();
    }

    public void calcularStockDisponible() {
        if (this.stock == null) {
            this.stock = 0;
        }
        if (this.stockReservado == null) {
            this.stockReservado = 0;
        }
        if (this.stock < 0) {
            this.stock = 0;
        }
        if (this.stockReservado < 0) {
            this.stockReservado = 0;
        }
        this.stockDisponible = this.stock - this.stockReservado;
        if (this.stockDisponible < 0) {
            this.stockDisponible = 0;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
        calcularStockDisponible();
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Integer getStockReservado() {
        return stockReservado;
    }

    public void setStockReservado(Integer stockReservado) {
        this.stockReservado = stockReservado;
        calcularStockDisponible();
    }

    public Integer getStockDisponible() {
        return stockDisponible;
    }

    public ImagenProducto getImagenProducto() {
        return imagenProducto;
    }

    public void setImagenProducto(ImagenProducto imagenProducto) {
        this.imagenProducto = imagenProducto;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
