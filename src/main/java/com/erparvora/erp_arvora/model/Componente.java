package com.erparvora.erp_arvora.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "componente")
public class Componente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Articulo producto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    private Articulo material;

    @Column(name = "nombre_pieza", length = 120)
    private String nombrePieza;

    @Column(name = "cantidad_piezas", nullable = false)
    private int cantidadPiezas = 1;

    @Column(name = "largo_mm", precision = 9, scale = 2)
    private BigDecimal largoMm;

    @Column(name = "ancho_mm", precision = 9, scale = 2)
    private BigDecimal anchoMm;

    @Column(name = "tolerancia_menos_mm", nullable = false, precision = 6, scale = 2)
    private BigDecimal toleranciaMenosMm = BigDecimal.ZERO;

    @Column(name = "tolerancia_mas_mm", nullable = false, precision = 6, scale = 2)
    private BigDecimal toleranciaMasMm = BigDecimal.ZERO;

    @Column(precision = 14, scale = 4)
    private BigDecimal cantidad;

    @Column(name = "merma_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal mermaPct = BigDecimal.ZERO;

    @Column(name = "orden", nullable = false)
    private short orden;

    @Column(length = 300)
    private String notas;

    public Componente() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Articulo getProducto() {
        return producto;
    }

    public void setProducto(Articulo producto) {
        this.producto = producto;
    }

    public Articulo getMaterial() {
        return material;
    }

    public void setMaterial(Articulo material) {
        this.material = material;
    }

    public String getNombrePieza() {
        return nombrePieza;
    }

    public void setNombrePieza(String nombrePieza) {
        this.nombrePieza = nombrePieza;
    }

    public int getCantidadPiezas() {
        return cantidadPiezas;
    }

    public void setCantidadPiezas(int cantidadPiezas) {
        this.cantidadPiezas = cantidadPiezas;
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

    public BigDecimal getToleranciaMenosMm() {
        return toleranciaMenosMm;
    }

    public void setToleranciaMenosMm(BigDecimal toleranciaMenosMm) {
        this.toleranciaMenosMm = toleranciaMenosMm;
    }

    public BigDecimal getToleranciaMasMm() {
        return toleranciaMasMm;
    }

    public void setToleranciaMasMm(BigDecimal toleranciaMasMm) {
        this.toleranciaMasMm = toleranciaMasMm;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getMermaPct() {
        return mermaPct;
    }

    public void setMermaPct(BigDecimal mermaPct) {
        this.mermaPct = mermaPct;
    }

    public short getOrden() {
        return orden;
    }

    public void setOrden(short orden) {
        this.orden = orden;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
