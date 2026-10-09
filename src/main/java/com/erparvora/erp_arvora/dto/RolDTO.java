package com.erparvora.erp_arvora.dto;

import com.erparvora.erp_arvora.model.Rol;

public record RolDTO(Long id, String codigo, String nombre, Boolean activo) {

    public static RolDTO from(Rol rol) {
        return new RolDTO(rol.getId(), rol.getCodigo(), rol.getNombre(), rol.isActivo());
    }
}
