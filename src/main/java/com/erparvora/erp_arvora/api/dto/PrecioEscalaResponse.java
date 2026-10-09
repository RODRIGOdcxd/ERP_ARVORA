package com.erparvora.erp_arvora.api.dto;

import java.math.BigDecimal;

public record PrecioEscalaResponse(Long id, Long articuloId, BigDecimal cantidadMinima, BigDecimal precioUnitario) {
}
