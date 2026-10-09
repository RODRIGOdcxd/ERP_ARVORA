package com.erparvora.erp_arvora.api.dto;

public record CategoriaResponse(Long id, String nombre, Long padreId, String padreNombre, boolean activo) {
}
