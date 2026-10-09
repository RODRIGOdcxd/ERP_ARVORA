package com.erparvora.erp_arvora.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es válido")
        @Size(max = 150, message = "El correo admite hasta 150 caracteres")
        String email,
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre admite hasta 120 caracteres")
        String nombre,
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        @Schema(description = "Obligatoria al crear. Al editar, omitirla deja la contraseña actual. Nunca se devuelve.")
        String password,
        @NotNull(message = "El rol es obligatorio")
        Long rolId,
        Boolean activo) {
}
