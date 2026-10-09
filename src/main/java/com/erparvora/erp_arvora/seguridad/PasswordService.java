package com.erparvora.erp_arvora.seguridad;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Solo el cifrado de contraseñas. La autenticación (Spring Security + JWT) va en un paso siguiente;
 * estos endpoints siguen abiertos a propósito.
 */
@Service
public class PasswordService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String hash(String clave) {
        return encoder.encode(clave);
    }

    public boolean coincide(String clave, String hash) {
        return hash != null && clave != null && encoder.matches(clave, hash);
    }
}
