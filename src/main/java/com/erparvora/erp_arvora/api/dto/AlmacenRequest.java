package com.erparvora.erp_arvora.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlmacenRequest(
        @NotBlank(message = "El código es obligatorio")
        @Size(max = 20) String codigo,
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100) String nombre,
        Boolean activo) {
}
