package com.erparvora.erp_arvora.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.erparvora.erp_arvora.model.TipoMovimientoInventario;

public record MovimientoResponse(
        Long id,
        OffsetDateTime fecha,
        TipoMovimientoInventario tipo,
        Long articuloId,
        String articuloCodigo,
        String articuloNombre,
        Long almacenId,
        String almacenNombre,
        BigDecimal cantidad,
        BigDecimal costoUnitario,
        Long proveedorId,
        Long cotizacionId,
        String documentoRef,
        Long usuarioId,
        String usuarioNombre,
        String nota,
        OffsetDateTime createdAt) {
}
