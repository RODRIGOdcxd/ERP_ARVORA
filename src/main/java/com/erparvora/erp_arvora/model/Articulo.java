package com.erparvora.erp_arvora.model;

import java.math.BigDecimal;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "articulo")
public class Articulo extends Auditable {

    /** Tasa de IGV incluida en {@link #precioVenta}. Las cotizaciones usan su propia tasa. */
    public static final BigDecimal TASA_IGV_LISTA = new BigDecimal("0.18");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String nombre;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "descripcion")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoArticulo tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidad_medida_id", nullable = false)
    private UnidadMedida unidadMedida;

    @Column(name = "se_compra", nullable = false)
    private boolean seCompra;

    @Column(name = "se_vende", nullable = false)
    private boolean seVende;

    @Column(name = "controla_stock", nullable = false)
    private boolean controlaStock = true;

    @Column(name = "largo_mm", precision = 9, scale = 2)
    private BigDecimal largoMm;

    @Column(name = "ancho_mm", precision = 9, scale = 2)
    private BigDecimal anchoMm;

    @Column(name = "alto_mm", precision = 9, scale = 2)
    private BigDecimal altoMm;

    @Column(name = "espesor_mm", precision = 9, scale = 2)
    private BigDecimal espesorMm;

    @Column(name = "diametro_mm", precision = 9, scale = 2)
    private BigDecimal diametroMm;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_corte", nullable = false, length = 10)
    private TipoCorte tipoCorte = TipoCorte.NINGUNO;

    @Column(name = "respeta_veta", nullable = false)
    private boolean respetaVeta;

    @Column(length = 80)
    private String marca;

    @Column(name = "color_acabado", length = 80)
    private String colorAcabado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_habitual_id")
    private Proveedor proveedorHabitual;

    @Column(name = "costo_referencia", precision = 14, scale = 4)
    private BigDecimal costoReferencia;

    /** Precio de lista unitario. Incluye IGV (18%). */
    @Column(name = "precio_venta", precision = 12, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "stock_minimo", nullable = false, precision = 14, scale = 4)
    private BigDecimal stockMinimo = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean activo = true;

    @Version
    @Column(nullable = false)
    private Long version;

    public Articulo() {
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

    public TipoArticulo getTipo() {
        return tipo;
    }

    public void setTipo(TipoArticulo tipo) {
        this.tipo = tipo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public boolean isSeCompra() {
        return seCompra;
    }

    public void setSeCompra(boolean seCompra) {
        this.seCompra = seCompra;
    }

    public boolean isSeVende() {
        return seVende;
    }

    public void setSeVende(boolean seVende) {
        this.seVende = seVende;
    }

    public boolean isControlaStock() {
        return controlaStock;
    }

    public void setControlaStock(boolean controlaStock) {
        this.controlaStock = controlaStock;
    }

    public BigDecimal getLargoMm() {
        return largoMm;
    }

    public void setLargoMm(BigDecimal largoMm) {
        this.largoMm = largoMm;
    }

    public BigDecimal getAnchoMm() {
        return anchoMm;
    }

    public void setAnchoMm(BigDecimal anchoMm) {
        this.anchoMm = anchoMm;
    }

    public BigDecimal getAltoMm() {
        return altoMm;
    }

    public void setAltoMm(BigDecimal altoMm) {
        this.altoMm = altoMm;
    }

    public BigDecimal getEspesorMm() {
        return espesorMm;
    }

    public void setEspesorMm(BigDecimal espesorMm) {
        this.espesorMm = espesorMm;
    }

    public BigDecimal getDiametroMm() {
        return diametroMm;
    }

    public void setDiametroMm(BigDecimal diametroMm) {
        this.diametroMm = diametroMm;
    }

    public TipoCorte getTipoCorte() {
        return tipoCorte;
    }

    public void setTipoCorte(TipoCorte tipoCorte) {
        this.tipoCorte = tipoCorte;
    }

    public boolean isRespetaVeta() {
        return respetaVeta;
    }

    public void setRespetaVeta(boolean respetaVeta) {
        this.respetaVeta = respetaVeta;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getColorAcabado() {
        return colorAcabado;
    }

    public void setColorAcabado(String colorAcabado) {
        this.colorAcabado = colorAcabado;
    }

    public Proveedor getProveedorHabitual() {
        return proveedorHabitual;
    }

    public void setProveedorHabitual(Proveedor proveedorHabitual) {
        this.proveedorHabitual = proveedorHabitual;
    }

    public BigDecimal getCostoReferencia() {
        return costoReferencia;
    }

    public void setCostoReferencia(BigDecimal costoReferencia) {
        this.costoReferencia = costoReferencia;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BigDecimal getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(BigDecimal stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
