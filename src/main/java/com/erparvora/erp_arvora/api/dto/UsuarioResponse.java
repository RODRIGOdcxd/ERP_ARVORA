package com.erparvora.erp_arvora.api.dto;

import java.time.OffsetDateTime;

public record UsuarioResponse(
        Long id,
        String email,
        String nombre,
        Long rolId,
        String rolCodigo,
        String rolNombre,
        boolean activo,
        OffsetDateTime ultimoAcceso,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
