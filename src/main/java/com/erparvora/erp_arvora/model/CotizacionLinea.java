package com.erparvora.erp_arvora.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "cotizacion_linea", uniqueConstraints = @UniqueConstraint(name = "ux_cot_linea", columnNames = {"cotizacion_id", "linea"}))
public class CotizacionLinea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cotizacion_id", nullable = false)
    private Cotizacion cotizacion;

    @Column(nullable = false)
    private short linea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "articulo_id")
    private Articulo articulo;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(name = "largo_mm", precision = 9, scale = 2)
    private BigDecimal largoMm;

    @Column(name = "ancho_mm", precision = 9, scale = 2)
    private BigDecimal anchoMm;

    @Column(name = "alto_mm", precision = 9, scale = 2)
    private BigDecimal altoMm;

    @Column(nullable = false, precision = 14, scale = 4)
    private BigDecimal cantidad;

    @Column(name = "unidad_codigo", nullable = false, length = 10)
    private String unidadCodigo = "UND";

    /** Foto del precio al cotizar. Incluye IGV si la cabecera tiene preciosIncluyenIgv. */
    @Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal descuento = new BigDecimal("0.00");

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal importe;

    @Column(name = "costo_unitario_est", precision = 14, scale = 4)
    private BigDecimal costoUnitarioEst;

    public CotizacionLinea() {
    }

    /** importe = cantidad * precioUnitario - descuento, redondeado a céntimos. */
    public void recalcularImporte() {
        BigDecimal cant = cantidad == null ? BigDecimal.ZERO : cantidad;
        BigDecimal precio = precioUnitario == null ? BigDecimal.ZERO : precioUnitario;
        BigDecimal desc = descuento == null ? BigDecimal.ZERO : descuento;
        this.importe = cant.multiply(precio).setScale(2, RoundingMode.HALF_UP).subtract(desc);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cotizacion getCotizacion() {
        return cotizacion;
    }

    public void setCotizacion(Cotizacion cotizacion) {
        this.cotizacion = cotizacion;
    }

    public short getLinea() {
        return linea;
    }

    public void setLinea(short linea) {
        this.linea = linea;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidadCodigo() {
        return unidadCodigo;
    }

    public void setUnidadCodigo(String unidadCodigo) {
        this.unidadCodigo = unidadCodigo;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    public BigDecimal getCostoUnitarioEst() {
        return costoUnitarioEst;
    }

    public void setCostoUnitarioEst(BigDecimal costoUnitarioEst) {
        this.costoUnitarioEst = costoUnitarioEst;
    }
}
