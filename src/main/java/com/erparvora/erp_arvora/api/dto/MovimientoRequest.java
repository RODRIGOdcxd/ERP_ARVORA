package com.erparvora.erp_arvora.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.erparvora.erp_arvora.model.TipoMovimientoInventario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record MovimientoRequest(
        @NotNull(message = "El tipo es obligatorio")
        TipoMovimientoInventario tipo,
        @NotNull(message = "El artículo es obligatorio")
        Long articuloId,
        @NotNull(message = "El almacén es obligatorio")
        Long almacenId,
        @NotNull(message = "La cantidad es obligatoria")
        @Digits(integer = 10, fraction = 4)
        @Schema(description = "Con signo: positivo entra, negativo sale. Compra, saldo inicial e ingreso de producción suman. Venta y consumo restan.")
        BigDecimal cantidad,
        @PositiveOrZero @Digits(integer = 10, fraction = 4)
        @Schema(description = "Costo unitario sin IGV. Obligatorio en una compra.")
        BigDecimal costoUnitario,
        Long proveedorId,
        Long cotizacionId,
        @Size(max = 60) String documentoRef,
        @NotNull(message = "El usuario es obligatorio")
        @Schema(description = "Quién registra el movimiento. Cuando exista el login, saldrá del token.")
        Long usuarioId,
        @Size(max = 300) String nota,
        OffsetDateTime fecha) {
}
