package com.erparvora.erp_arvora.api.dto;

import java.time.OffsetDateTime;

import com.erparvora.erp_arvora.model.CanalOrigen;
import com.erparvora.erp_arvora.model.TipoDocumento;

public record ClienteResponse(
        Long id,
        TipoDocumento tipoDocumento,
        String numeroDocumento,
        String nombre,
        String telefono,
        String email,
        String direccion,
        String ciudad,
        CanalOrigen canalOrigen,
        String notas,
        boolean activo,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
