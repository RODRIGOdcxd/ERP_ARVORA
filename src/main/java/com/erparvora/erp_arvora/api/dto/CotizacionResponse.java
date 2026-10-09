package com.erparvora.erp_arvora.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import com.erparvora.erp_arvora.model.EstadoCotizacion;
import com.erparvora.erp_arvora.model.Moneda;

public record CotizacionResponse(
        Long id,
        String numero,
        Long clienteId,
        String clienteNombre,
        Long usuarioId,
        String usuarioNombre,
        LocalDate fechaEmision,
        LocalDate validaHasta,
        EstadoCotizacion estado,
        Moneda moneda,
        boolean preciosIncluyenIgv,
        BigDecimal tasaIgv,
        BigDecimal subtotal,
        BigDecimal descuento,
        BigDecimal igv,
        BigDecimal total,
        BigDecimal adelantoPct,
        Short plazoEntregaDias,
        String condiciones,
        String notasInternas,
        Long version,
        List<CotizacionLineaResponse> lineas,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
