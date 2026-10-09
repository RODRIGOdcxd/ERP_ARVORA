package com.erparvora.erp_arvora.api.dto;

import java.math.BigDecimal;

import com.erparvora.erp_arvora.model.TipoArticulo;
import com.erparvora.erp_arvora.model.TipoCorte;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ArticuloRequest(
        @NotBlank(message = "El código es obligatorio")
        @Size(max = 40) String codigo,
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 200) String nombre,
        String descripcion,
        @NotNull(message = "El tipo es obligatorio")
        TipoArticulo tipo,
        Long categoriaId,
        @NotNull(message = "La unidad de medida es obligatoria")
        Long unidadMedidaId,
        Boolean seCompra,
        Boolean seVende,
        @Schema(description = "Un servicio siempre queda en falso")
        Boolean controlaStock,
        @Positive(message = "El largo debe ser mayor que cero")
        @Digits(integer = 7, fraction = 2) BigDecimal largoMm,
        @Positive @Digits(integer = 7, fraction = 2) BigDecimal anchoMm,
        @Positive @Digits(integer = 7, fraction = 2) BigDecimal altoMm,
        @Positive @Digits(integer = 7, fraction = 2) BigDecimal espesorMm,
        @Positive @Digits(integer = 7, fraction = 2) BigDecimal diametroMm,
        TipoCorte tipoCorte,
        Boolean respetaVeta,
        @Size(max = 80) String marca,
        @Size(max = 80) String colorAcabado,
        Long proveedorHabitualId,
        @PositiveOrZero @Digits(integer = 10, fraction = 4)
        @Schema(description = "Costo unitario de referencia, sin IGV")
        BigDecimal costoReferencia,
        @PositiveOrZero @Digits(integer = 10, fraction = 2)
        @Schema(description = "Precio de lista unitario con IGV incluido (18%)")
        BigDecimal precioVenta,
        @PositiveOrZero @Digits(integer = 10, fraction = 4) BigDecimal stockMinimo,
        Boolean activo,
        @Schema(description = "Obligatoria al editar. Viene en la respuesta.")
        Long version) {
}
