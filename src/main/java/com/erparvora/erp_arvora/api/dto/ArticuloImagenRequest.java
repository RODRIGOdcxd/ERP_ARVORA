package com.erparvora.erp_arvora.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ArticuloImagenRequest(
        @NotBlank(message = "La URL es obligatoria")
        @Size(max = 500) String url,
        Integer orden,
        Boolean principal) {
}
