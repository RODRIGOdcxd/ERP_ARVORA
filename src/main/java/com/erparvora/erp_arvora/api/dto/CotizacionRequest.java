package com.erparvora.erp_arvora.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.erparvora.erp_arvora.model.EstadoCotizacion;
import com.erparvora.erp_arvora.model.Moneda;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CotizacionRequest(
        @Size(max = 20) @Schema(description = "Si se omite, se genera COT-AAAA-00001")
        String numero,
        @NotNull(message = "El cliente es obligatorio")
        Long clienteId,
        @NotNull(message = "El usuario es obligatorio")
        Long usuarioId,
        LocalDate fechaEmision,
        LocalDate validaHasta,
        EstadoCotizacion estado,
        Moneda moneda,
        @Schema(description = "Verdadero si los precios de línea incluyen IGV. Por defecto verdadero, como el precio de lista.")
        Boolean preciosIncluyenIgv,
        @DecimalMin(value = "0", message = "La tasa de IGV no puede ser negativa")
        @DecimalMax(value = "0.9999", message = "La tasa de IGV debe ser menor que 1")
        @Digits(integer = 1, fraction = 4)
        @Schema(description = "Por defecto 0.18", example = "0.1800")
        BigDecimal tasaIgv,
        @PositiveOrZero @Digits(integer = 10, fraction = 2) BigDecimal descuento,
        @DecimalMin("0") @DecimalMax("100") @Digits(integer = 3, fraction = 2) BigDecimal adelantoPct,
        @Min(0) @Max(3650) Integer plazoEntregaDias,
        String condiciones,
        String notasInternas,
        @Schema(description = "Obligatoria al editar")
        Long version) {
}
