package com.erparvora.erp_arvora.api.dto;

public record ArticuloImagenResponse(Long id, Long articuloId, String url, short orden, boolean principal) {
}
