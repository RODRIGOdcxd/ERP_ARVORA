package com.erparvora.erp_arvora.dto;

public class CategoriaDTO {
    private String nombre;
    private String descripcion;

    public CategoriaDTO() {}

    public CategoriaDTO(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}