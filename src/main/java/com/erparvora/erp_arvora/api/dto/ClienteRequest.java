package com.erparvora.erp_arvora.api.dto;

import com.erparvora.erp_arvora.model.CanalOrigen;
import com.erparvora.erp_arvora.model.TipoDocumento;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        TipoDocumento tipoDocumento,
        @Size(max = 20, message = "El número de documento admite hasta 20 caracteres")
        String numeroDocumento,
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 200, message = "El nombre admite hasta 200 caracteres")
        String nombre,
        @Size(max = 30) String telefono,
        @Email(message = "El correo no es válido")
        @Size(max = 150) String email,
        @Size(max = 300) String direccion,
        @Size(max = 80) String ciudad,
        CanalOrigen canalOrigen,
        String notas,
        Boolean activo) {
}
