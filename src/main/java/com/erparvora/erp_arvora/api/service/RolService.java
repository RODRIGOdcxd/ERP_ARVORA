package com.erparvora.erp_arvora.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erparvora.erp_arvora.api.Busqueda;
import com.erparvora.erp_arvora.api.dto.RolRequest;
import com.erparvora.erp_arvora.api.dto.RolResponse;
import com.erparvora.erp_arvora.exception.RecursoNoEncontradoException;
import com.erparvora.erp_arvora.model.Rol;
import com.erparvora.erp_arvora.repository.RolRepository;

import jakarta.persistence.criteria.Predicate;

@Service("rolApiService")
public class RolService {

    private final RolRepository repository;

    public RolService(RolRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<RolResponse> listar(String texto, Boolean activo, Pageable pageable) {
        return repository.findAll(filtrar(texto, activo), pageable).map(RolService::respuesta);
    }

    @Transactional(readOnly = true)
    public RolResponse obtener(Long id) {
        return respuesta(buscar(id));
    }

    @Transactional
    public RolResponse crear(RolRequest request) {
        Rol rol = new Rol();
        aplicar(rol, request, true);
        return respuesta(repository.save(rol));
    }

    @Transactional
    public RolResponse actualizar(Long id, RolRequest request) {
        Rol rol = buscar(id);
        aplicar(rol, request, false);
        return respuesta(repository.save(rol));
    }

    @Transactional
    public void eliminar(Long id) {
        Rol rol = buscar(id);
        rol.setActivo(false);
        repository.save(rol);
    }

    private Rol buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("el rol", id));
    }

    private static void aplicar(Rol rol, RolRequest request, boolean creando) {
        rol.setCodigo(request.codigo().trim());
        rol.setNombre(request.nombre().trim());
        if (request.activo() != null) {
            rol.setActivo(request.activo());
        } else if (creando) {
            rol.setActivo(true);
        }
    }

    private static RolResponse respuesta(Rol rol) {
        return new RolResponse(rol.getId(), rol.getCodigo(), rol.getNombre(), rol.isActivo());
    }

    private static Specification<Rol> filtrar(String texto, Boolean activo) {
        return (root, query, cb) -> {
            List<Predicate> partes = new ArrayList<>();
            if (Busqueda.presente(texto)) {
                String patron = Busqueda.patron(texto);
                partes.add(cb.or(
                        cb.like(cb.lower(root.get("codigo")), patron, '\\'),
                        cb.like(cb.lower(root.get("nombre")), patron, '\\')));
            }
            if (activo != null) {
                partes.add(cb.equal(root.get("activo"), activo));
            }
            return partes.isEmpty() ? cb.conjunction() : cb.and(partes.toArray(Predicate[]::new));
        };
    }
}
