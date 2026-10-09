package com.erparvora.erp_arvora.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erparvora.erp_arvora.api.Busqueda;
import com.erparvora.erp_arvora.api.dto.AlmacenRequest;
import com.erparvora.erp_arvora.api.dto.AlmacenResponse;
import com.erparvora.erp_arvora.exception.RecursoNoEncontradoException;
import com.erparvora.erp_arvora.model.Almacen;
import com.erparvora.erp_arvora.repository.AlmacenRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class AlmacenService {

    private final AlmacenRepository repository;

    public AlmacenService(AlmacenRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<AlmacenResponse> listar(String texto, Boolean activo, Pageable pageable) {
        return repository.findAll(filtrar(texto, activo), pageable).map(AlmacenService::respuesta);
    }

    @Transactional(readOnly = true)
    public AlmacenResponse obtener(Long id) {
        return respuesta(buscar(id));
    }

    @Transactional
    public AlmacenResponse crear(AlmacenRequest request) {
        Almacen almacen = new Almacen();
        aplicar(almacen, request, true);
        return respuesta(repository.save(almacen));
    }

    @Transactional
    public AlmacenResponse actualizar(Long id, AlmacenRequest request) {
        Almacen almacen = buscar(id);
        aplicar(almacen, request, false);
        return respuesta(repository.save(almacen));
    }

    @Transactional
    public void eliminar(Long id) {
        Almacen almacen = buscar(id);
        almacen.setActivo(false);
        repository.save(almacen);
    }

    private Almacen buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("el almacén", id));
    }

    private static void aplicar(Almacen almacen, AlmacenRequest request, boolean creando) {
        almacen.setCodigo(request.codigo().trim());
        almacen.setNombre(request.nombre().trim());
        if (request.activo() != null) {
            almacen.setActivo(request.activo());
        } else if (creando) {
            almacen.setActivo(true);
        }
    }

    private static AlmacenResponse respuesta(Almacen almacen) {
        return new AlmacenResponse(almacen.getId(), almacen.getCodigo(), almacen.getNombre(), almacen.isActivo());
    }

    private static Specification<Almacen> filtrar(String texto, Boolean activo) {
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
