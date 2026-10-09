package com.erparvora.erp_arvora.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.erparvora.erp_arvora.model.TipoArticulo;
import com.erparvora.erp_arvora.model.TipoCorte;

public record ArticuloResponse(
        Long id,
        String codigo,
        String nombre,
        String descripcion,
        TipoArticulo tipo,
        Long categoriaId,
        String categoriaNombre,
        Long unidadMedidaId,
        String unidadMedidaCodigo,
        boolean seCompra,
        boolean seVende,
        boolean controlaStock,
        BigDecimal largoMm,
        BigDecimal anchoMm,
        BigDecimal altoMm,
        BigDecimal espesorMm,
        BigDecimal diametroMm,
        TipoCorte tipoCorte,
        boolean respetaVeta,
        String marca,
        String colorAcabado,
        Long proveedorHabitualId,
        String proveedorNombre,
        BigDecimal costoReferencia,
        BigDecimal precioVenta,
        boolean preciosIncluyenIgv,
        BigDecimal stockMinimo,
        boolean activo,
        Long version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
