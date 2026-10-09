package com.erparvora.erp_arvora.api.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CotizacionLineaRequest(
        @NotNull(message = "El número de línea es obligatorio")
        @Min(1) @Max(9999) Integer linea,
        @Schema(description = "Vacío si el ítem no está en el catálogo")
        Long articuloId,
        @Size(max = 500) String descripcion,
        @Positive @Digits(integer = 7, fraction = 2) BigDecimal largoMm,
        @Positive @Digits(integer = 7, fraction = 2) BigDecimal anchoMm,
        @Positive @Digits(integer = 7, fraction = 2) BigDecimal altoMm,
        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor que cero")
        @Digits(integer = 10, fraction = 4) BigDecimal cantidad,
        @Size(max = 10) String unidadCodigo,
        @PositiveOrZero @Digits(integer = 10, fraction = 2)
        @Schema(description = "Si se omite y hay artículo, se copia el precio de lista (con IGV)")
        BigDecimal precioUnitario,
        @PositiveOrZero @Digits(integer = 10, fraction = 2) BigDecimal descuento) {
}
