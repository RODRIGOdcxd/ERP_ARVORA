package com.erparvora.erp_arvora.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProveedorRequest(
        @Pattern(regexp = "^(10|15|17|20)[0-9]{9}$", message = "El RUC debe tener 11 dígitos y empezar por 10, 15, 17 o 20")
        String ruc,
        @NotBlank(message = "La razón social es obligatoria")
        @Size(max = 200) String razonSocial,
        @Size(max = 200) String nombreComercial,
        @Size(max = 120) String contacto,
        @Size(max = 30) String telefono,
        @Email(message = "El correo no es válido")
        @Size(max = 150) String email,
        @Size(max = 300) String direccion,
        @Size(max = 80) String rubro,
        String notas,
        Boolean activo) {
}
