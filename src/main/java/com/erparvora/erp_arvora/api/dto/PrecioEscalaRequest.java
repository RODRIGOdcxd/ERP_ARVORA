package com.erparvora.erp_arvora.api.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record PrecioEscalaRequest(
        @NotNull(message = "La cantidad mínima es obligatoria")
        @Positive(message = "La cantidad mínima debe ser mayor que cero")
        @Digits(integer = 10, fraction = 4) BigDecimal cantidadMinima,
        @NotNull(message = "El precio es obligatorio")
        @PositiveOrZero(message = "El precio no puede ser negativo")
        @Digits(integer = 10, fraction = 2)
        @Schema(description = "Precio unitario con IGV incluido, igual que el precio de lista")
        BigDecimal precioUnitario) {
}
