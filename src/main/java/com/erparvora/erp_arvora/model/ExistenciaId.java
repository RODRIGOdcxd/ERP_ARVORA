package com.erparvora.erp_arvora.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ExistenciaId implements Serializable {

    @Column(name = "articulo_id")
    private Long articuloId;

    @Column(name = "almacen_id")
    private Long almacenId;

    public ExistenciaId() {
    }

    public ExistenciaId(Long articuloId, Long almacenId) {
        this.articuloId = articuloId;
        this.almacenId = almacenId;
    }

    public Long getArticuloId() {
        return articuloId;
    }

    public void setArticuloId(Long articuloId) {
        this.articuloId = articuloId;
    }

    public Long getAlmacenId() {
        return almacenId;
    }

    public void setAlmacenId(Long almacenId) {
        this.almacenId = almacenId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ExistenciaId that)) {
            return false;
        }
        return Objects.equals(articuloId, that.articuloId) && Objects.equals(almacenId, that.almacenId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(articuloId, almacenId);
    }
}
