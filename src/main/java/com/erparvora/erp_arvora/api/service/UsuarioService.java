package com.erparvora.erp_arvora.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erparvora.erp_arvora.api.Busqueda;
import com.erparvora.erp_arvora.api.dto.UsuarioRequest;
import com.erparvora.erp_arvora.api.dto.UsuarioResponse;
import com.erparvora.erp_arvora.exception.RecursoNoEncontradoException;
import com.erparvora.erp_arvora.exception.SolicitudInvalidaException;
import com.erparvora.erp_arvora.model.Rol;
import com.erparvora.erp_arvora.model.Usuario;
import com.erparvora.erp_arvora.repository.UsuarioRepository;
import com.erparvora.erp_arvora.seguridad.PasswordService;

import jakarta.persistence.criteria.Predicate;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final Referencias referencias;
    private final PasswordService passwords;

    public UsuarioService(UsuarioRepository repository, Referencias referencias, PasswordService passwords) {
        this.repository = repository;
        this.referencias = referencias;
        this.passwords = passwords;
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listar(String texto, Long rolId, Boolean activo, Pageable pageable) {
        return repository.findAll(filtrar(texto, rolId, activo), pageable).map(this::respuesta);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return respuesta(buscar(id));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest request) {
        if (request.password() == null || request.password().isBlank()) {
            throw new SolicitudInvalidaException("La contraseña es obligatoria al crear el usuario");
        }
        Usuario usuario = new Usuario();
        aplicar(usuario, request, true);
        return respuesta(repository.save(usuario));
    }

    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = buscar(id);
        aplicar(usuario, request, false);
        return respuesta(repository.save(usuario));
    }

    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = buscar(id);
        usuario.setActivo(false);
        repository.save(usuario);
    }

    private Usuario buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("el usuario", id));
    }

    private void aplicar(Usuario usuario, UsuarioRequest request, boolean creando) {
        usuario.setEmail(request.email().trim().toLowerCase(Locale.ROOT));
        usuario.setNombre(request.nombre().trim());
        usuario.setRol(referencias.rol(request.rolId()));
        if (request.password() != null && !request.password().isBlank()) {
            usuario.setPasswordHash(passwords.hash(request.password()));
        }
        if (request.activo() != null) {
            usuario.setActivo(request.activo());
        } else if (creando) {
            usuario.setActivo(true);
        }
    }

    private UsuarioResponse respuesta(Usuario usuario) {
        Rol rol = usuario.getRol();
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNombre(),
                rol.getId(),
                rol.getCodigo(),
                rol.getNombre(),
                usuario.isActivo(),
                usuario.getUltimoAcceso(),
                usuario.getCreatedAt(),
                usuario.getUpdatedAt());
    }

    private static Specification<Usuario> filtrar(String texto, Long rolId, Boolean activo) {
        return (root, query, cb) -> {
            List<Predicate> partes = new ArrayList<>();
            if (Busqueda.presente(texto)) {
                String patron = Busqueda.patron(texto);
                partes.add(cb.or(
                        cb.like(cb.lower(root.get("email")), patron, '\\'),
                        cb.like(cb.lower(root.get("nombre")), patron, '\\')));
            }
            if (rolId != null) {
                partes.add(cb.equal(root.get("rol").get("id"), rolId));
            }
            if (activo != null) {
                partes.add(cb.equal(root.get("activo"), activo));
            }
            return partes.isEmpty() ? cb.conjunction() : cb.and(partes.toArray(Predicate[]::new));
        };
    }
}
