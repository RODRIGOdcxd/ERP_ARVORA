package com.erparvora.erp_arvora.api.dto;

import com.erparvora.erp_arvora.model.Magnitud;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UnidadMedidaRequest(
        @NotBlank(message = "El código es obligatorio")
        @Size(max = 10) String codigo,
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50) String nombre,
        @NotNull(message = "La magnitud es obligatoria")
        Magnitud magnitud,
        @NotNull(message = "Los decimales son obligatorios")
        @Min(value = 0, message = "Los decimales van de 0 a 4")
        @Max(value = 4, message = "Los decimales van de 0 a 4")
        Integer decimales) {
}
