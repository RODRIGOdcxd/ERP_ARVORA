package com.erparvora.erp_arvora.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collection;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "cotizacion")
public class Cotizacion extends Auditable {

    private static final int ESCALA_DINERO = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "valida_hasta")
    private LocalDate validaHasta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private EstadoCotizacion estado = EstadoCotizacion.BORRADOR;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(nullable = false, length = 3)
    private Moneda moneda = Moneda.PEN;

    /** Los precios de línea (y el de lista) incluyen IGV cuando es verdadero. */
    @Column(name = "precios_incluyen_igv", nullable = false)
    private boolean preciosIncluyenIgv = true;

    @Column(name = "tasa_igv", nullable = false, precision = 5, scale = 4)
    private BigDecimal tasaIgv = new BigDecimal("0.1800");

    /** Base imponible, sin IGV. */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = new BigDecimal("0.00");

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal descuento = new BigDecimal("0.00");

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal igv = new BigDecimal("0.00");

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total = new BigDecimal("0.00");

    @Column(name = "adelanto_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal adelantoPct = new BigDecimal("50.00");

    @Column(name = "plazo_entrega_dias")
    private Short plazoEntregaDias;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "condiciones")
    private String condiciones;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "notas_internas")
    private String notasInternas;

    @Version
    @Column(nullable = false)
    private Long version;

    public Cotizacion() {
    }

    @PrePersist
    void completarFechaEmision() {
        if (fechaEmision == null) {
            fechaEmision = LocalDate.now(ZoneOffset.UTC);
        }
    }

    /**
     * Recalcula subtotal (base imponible, sin IGV), IGV y total a partir de la suma
     * de importes de línea, restando el descuento de cabecera.
     * Si los precios incluyen IGV, esa suma ya trae el impuesto y el subtotal sale
     * de dividir el neto entre (1 + tasaIgv).
     */
    public void recalcularTotales(BigDecimal sumaImportesLinea) {
        BigDecimal bruto = sumaImportesLinea == null ? BigDecimal.ZERO : sumaImportesLinea;
        BigDecimal desc = descuento == null ? BigDecimal.ZERO : descuento;
        BigDecimal neto = bruto.subtract(desc).setScale(ESCALA_DINERO, RoundingMode.HALF_UP);
        if (neto.signum() < 0) {
            throw new IllegalArgumentException("El descuento no puede superar la suma de las líneas");
        }
        BigDecimal tasa = tasaIgv == null ? Articulo.TASA_IGV_LISTA : tasaIgv;
        if (preciosIncluyenIgv) {
            BigDecimal factor = BigDecimal.ONE.add(tasa);
            this.total = neto;
            this.subtotal = neto.divide(factor, ESCALA_DINERO, RoundingMode.HALF_UP);
            this.igv = this.total.subtract(this.subtotal);
        } else {
            this.subtotal = neto;
            this.igv = neto.multiply(tasa).setScale(ESCALA_DINERO, RoundingMode.HALF_UP);
            this.total = this.subtotal.add(this.igv);
        }
    }

    public void recalcularTotalesDesdeLineas(Collection<CotizacionLinea> lineas) {
        BigDecimal suma = BigDecimal.ZERO;
        if (lineas != null) {
            for (CotizacionLinea linea : lineas) {
                if (linea.getImporte() != null) {
                    suma = suma.add(linea.getImporte());
                }
            }
        }
        recalcularTotales(suma);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public LocalDate getValidaHasta() {
        return validaHasta;
    }

    public void setValidaHasta(LocalDate validaHasta) {
        this.validaHasta = validaHasta;
    }

    public EstadoCotizacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoCotizacion estado) {
        this.estado = estado;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }

    public boolean isPreciosIncluyenIgv() {
        return preciosIncluyenIgv;
    }

    public void setPreciosIncluyenIgv(boolean preciosIncluyenIgv) {
        this.preciosIncluyenIgv = preciosIncluyenIgv;
    }

    public BigDecimal getTasaIgv() {
        return tasaIgv;
    }

    public void setTasaIgv(BigDecimal tasaIgv) {
        this.tasaIgv = tasaIgv;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getIgv() {
        return igv;
    }

    public void setIgv(BigDecimal igv) {
        this.igv = igv;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getAdelantoPct() {
        return adelantoPct;
    }

    public void setAdelantoPct(BigDecimal adelantoPct) {
        this.adelantoPct = adelantoPct;
    }

    public Short getPlazoEntregaDias() {
        return plazoEntregaDias;
    }

    public void setPlazoEntregaDias(Short plazoEntregaDias) {
        this.plazoEntregaDias = plazoEntregaDias;
    }

    public String getCondiciones() {
        return condiciones;
    }

    public void setCondiciones(String condiciones) {
        this.condiciones = condiciones;
    }

    public String getNotasInternas() {
        return notasInternas;
    }

    public void setNotasInternas(String notasInternas) {
        this.notasInternas = notasInternas;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
