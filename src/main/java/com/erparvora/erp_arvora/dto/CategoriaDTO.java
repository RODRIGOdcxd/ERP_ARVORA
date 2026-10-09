package com.erparvora.erp_arvora.dto;

import com.erparvora.erp_arvora.model.Categoria;

public record CategoriaDTO(Long id, String nombre, Long padreId, Boolean activo) {

    public static CategoriaDTO from(Categoria categoria) {
        Categoria padre = categoria.getPadre();
        return new CategoriaDTO(
                categoria.getId(),
                categoria.getNombre(),
                padre == null ? null : padre.getId(),
                categoria.isActivo());
    }
}
