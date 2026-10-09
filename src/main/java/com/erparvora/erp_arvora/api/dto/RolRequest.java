package com.erparvora.erp_arvora.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RolRequest(
        @NotBlank(message = "El código es obligatorio")
        @Size(max = 30, message = "El código admite hasta 30 caracteres")
        @Schema(example = "VENTAS")
        String codigo,
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre admite hasta 80 caracteres")
        String nombre,
        @Schema(description = "Si se omite al crear, el rol queda activo")
        Boolean activo) {
}
