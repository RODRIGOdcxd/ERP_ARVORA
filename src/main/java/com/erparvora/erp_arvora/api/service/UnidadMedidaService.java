package com.erparvora.erp_arvora.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erparvora.erp_arvora.api.Busqueda;
import com.erparvora.erp_arvora.api.dto.UnidadMedidaRequest;
import com.erparvora.erp_arvora.api.dto.UnidadMedidaResponse;
import com.erparvora.erp_arvora.exception.RecursoNoEncontradoException;
import com.erparvora.erp_arvora.model.UnidadMedida;
import com.erparvora.erp_arvora.repository.UnidadMedidaRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class UnidadMedidaService {

    private final UnidadMedidaRepository repository;

    public UnidadMedidaService(UnidadMedidaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<UnidadMedidaResponse> listar(String texto, Pageable pageable) {
        return repository.findAll(filtrar(texto), pageable).map(UnidadMedidaService::respuesta);
    }

    @Transactional(readOnly = true)
    public UnidadMedidaResponse obtener(Long id) {
        return respuesta(buscar(id));
    }

    @Transactional
    public UnidadMedidaResponse crear(UnidadMedidaRequest request) {
        UnidadMedida unidad = new UnidadMedida();
        aplicar(unidad, request);
        return respuesta(repository.save(unidad));
    }

    @Transactional
    public UnidadMedidaResponse actualizar(Long id, UnidadMedidaRequest request) {
        UnidadMedida unidad = buscar(id);
        aplicar(unidad, request);
        return respuesta(repository.save(unidad));
    }

    @Transactional
    public void eliminar(Long id) {
        UnidadMedida unidad = buscar(id);
        repository.delete(unidad);
    }

    private UnidadMedida buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("la unidad de medida", id));
    }

    private static void aplicar(UnidadMedida unidad, UnidadMedidaRequest request) {
        unidad.setCodigo(request.codigo().trim());
        unidad.setNombre(request.nombre().trim());
        unidad.setMagnitud(request.magnitud());
        unidad.setDecimales(request.decimales().shortValue());
    }

    private static UnidadMedidaResponse respuesta(UnidadMedida unidad) {
        return new UnidadMedidaResponse(unidad.getId(), unidad.getCodigo(), unidad.getNombre(), unidad.getMagnitud(),
                unidad.getDecimales());
    }

    private static Specification<UnidadMedida> filtrar(String texto) {
        return (root, query, cb) -> {
            if (!Busqueda.presente(texto)) {
                return cb.conjunction();
            }
            String patron = Busqueda.patron(texto);
            List<Predicate> partes = new ArrayList<>();
            partes.add(cb.or(
                    cb.like(cb.lower(root.get("codigo")), patron, '\\'),
                    cb.like(cb.lower(root.get("nombre")), patron, '\\')));
            return cb.and(partes.toArray(Predicate[]::new));
        };
    }
}
