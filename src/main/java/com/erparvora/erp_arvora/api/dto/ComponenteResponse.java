package com.erparvora.erp_arvora.api.dto;

import java.math.BigDecimal;

public record ComponenteResponse(
        Long id,
        Long productoId,
        Long materialId,
        String materialCodigo,
        String materialNombre,
        String nombrePieza,
        int cantidadPiezas,
        BigDecimal largoMm,
        BigDecimal anchoMm,
        BigDecimal toleranciaMenosMm,
        BigDecimal toleranciaMasMm,
        BigDecimal cantidad,
        BigDecimal mermaPct,
        short orden,
        String notas) {
}
