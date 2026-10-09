package com.erparvora.erp_arvora.api.dto;

import java.math.BigDecimal;

public record CotizacionLineaResponse(
        Long id,
        short linea,
        Long articuloId,
        String descripcion,
        BigDecimal largoMm,
        BigDecimal anchoMm,
        BigDecimal altoMm,
        BigDecimal cantidad,
        String unidadCodigo,
        BigDecimal precioUnitario,
        BigDecimal descuento,
        BigDecimal importe,
        BigDecimal costoUnitarioEst) {
}
