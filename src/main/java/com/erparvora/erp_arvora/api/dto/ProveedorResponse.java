package com.erparvora.erp_arvora.api.dto;

import java.time.OffsetDateTime;

public record ProveedorResponse(
        Long id,
        String ruc,
        String razonSocial,
        String nombreComercial,
        String contacto,
        String telefono,
        String email,
        String direccion,
        String rubro,
        String notas,
        boolean activo,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
