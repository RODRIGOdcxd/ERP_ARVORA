package com.erparvora.erp_arvora.api.dto;

import java.math.BigDecimal;

public record ExistenciaResponse(
        Long articuloId,
        String articuloCodigo,
        String articuloNombre,
        Long almacenId,
        String almacenCodigo,
        String almacenNombre,
        BigDecimal cantidad) {
}
