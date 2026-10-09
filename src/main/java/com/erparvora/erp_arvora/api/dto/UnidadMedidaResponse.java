package com.erparvora.erp_arvora.api.dto;

import com.erparvora.erp_arvora.model.Magnitud;

public record UnidadMedidaResponse(Long id, String codigo, String nombre, Magnitud magnitud, short decimales) {
}
