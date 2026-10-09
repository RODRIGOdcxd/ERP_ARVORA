package com.erparvora.erp_arvora.api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ComponenteRequest(
        @NotNull(message = "El material es obligatorio")
        Long materialId,
        @Size(max = 120) String nombrePieza,
        @Positive(message = "La cantidad de piezas debe ser mayor que cero")
        Integer cantidadPiezas,
        @Positive @Digits(integer = 7, fraction = 2) BigDecimal largoMm,
        @Positive @Digits(integer = 7, fraction = 2) BigDecimal anchoMm,
        @PositiveOrZero @Digits(integer = 4, fraction = 2) BigDecimal toleranciaMenosMm,
        @PositiveOrZero @Digits(integer = 4, fraction = 2) BigDecimal toleranciaMasMm,
        @Positive @Digits(integer = 10, fraction = 4) BigDecimal cantidad,
        @DecimalMin(value = "0", message = "La merma no puede ser negativa")
        @DecimalMax(value = "99.99", message = "La merma debe ser menor que 100")
        @Digits(integer = 3, fraction = 2) BigDecimal mermaPct,
        Integer orden,
        @Size(max = 300) String notas) {
}
