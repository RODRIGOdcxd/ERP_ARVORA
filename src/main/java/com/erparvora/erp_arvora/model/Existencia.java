package com.erparvora.erp_arvora.model;

import java.math.BigDecimal;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Immutable
@Table(name = "v_existencia")
public class Existencia {

    @EmbeddedId
    private ExistenciaId id;

    @Column(name = "cantidad", precision = 14, scale = 4)
    private BigDecimal cantidad;

    public Existencia() {
    }

    public ExistenciaId getId() {
        return id;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }
}
