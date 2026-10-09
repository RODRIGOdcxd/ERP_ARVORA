package com.erparvora.erp_arvora.exception;

public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public static RecursoNoEncontradoException de(String nombre, Long id) {
        return new RecursoNoEncontradoException("No se encontró " + nombre + " con id " + id);
    }
}
