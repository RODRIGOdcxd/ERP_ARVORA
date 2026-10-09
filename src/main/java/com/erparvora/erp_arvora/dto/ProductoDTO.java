package com.erparvora.erp_arvora.dto;

import java.math.BigDecimal;

import com.erparvora.erp_arvora.model.Articulo;
import com.erparvora.erp_arvora.model.Categoria;

/** Precio de lista de un artículo tipo PRODUCTO. precioVenta incluye IGV (18%). */
public record ProductoDTO(
        Long id,
        String codigo,
        String nombre,
        String descripcion,
        String tipo,
        Long categoriaId,
        String categoriaNombre,
        String unidadMedidaCodigo,
        BigDecimal precioVenta,
        boolean preciosIncluyenIgv) {

    public static ProductoDTO from(Articulo articulo) {
        Categoria categoria = articulo.getCategoria();
        return new ProductoDTO(
                articulo.getId(),
                articulo.getCodigo(),
                articulo.getNombre(),
                articulo.getDescripcion(),
                articulo.getTipo().name(),
                categoria == null ? null : categoria.getId(),
                categoria == null ? null : categoria.getNombre(),
                articulo.getUnidadMedida().getCodigo(),
                articulo.getPrecioVenta(),
                true);
    }
}
