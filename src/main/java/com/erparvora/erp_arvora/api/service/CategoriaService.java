package com.erparvora.erp_arvora.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erparvora.erp_arvora.api.Busqueda;
import com.erparvora.erp_arvora.api.dto.CategoriaRequest;
import com.erparvora.erp_arvora.api.dto.CategoriaResponse;
import com.erparvora.erp_arvora.exception.RecursoNoEncontradoException;
import com.erparvora.erp_arvora.exception.SolicitudInvalidaException;
import com.erparvora.erp_arvora.model.Categoria;
import com.erparvora.erp_arvora.repository.CategoriaRepository;

import jakarta.persistence.criteria.Predicate;

@Service("categoriaApiService")
public class CategoriaService {

    private final CategoriaRepository repository;
    private final Referencias referencias;

    public CategoriaService(CategoriaRepository repository, Referencias referencias) {
        this.repository = repository;
        this.referencias = referencias;
    }

    @Transactional(readOnly = true)
    public Page<CategoriaResponse> listar(String texto, Boolean activo, Pageable pageable) {
        return repository.findAll(filtrar(texto, activo), pageable).map(CategoriaService::respuesta);
    }

    @Transactional(readOnly = true)
    public CategoriaResponse obtener(Long id) {
        return respuesta(buscar(id));
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        Categoria categoria = new Categoria();
        aplicar(categoria, request, true);
        return respuesta(repository.save(categoria));
    }

    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscar(id);
        if (request.padreId() != null && request.padreId().equals(id)) {
            throw new SolicitudInvalidaException("Una categoría no puede ser su propio padre");
        }
        aplicar(categoria, request, false);
        return respuesta(repository.save(categoria));
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = buscar(id);
        categoria.setActivo(false);
        repository.save(categoria);
    }

    private Categoria buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("la categoría", id));
    }

    private void aplicar(Categoria categoria, CategoriaRequest request, boolean creando) {
        categoria.setNombre(request.nombre().trim());
        if (request.padreId() == null) {
            categoria.setPadre(null);
        } else {
            categoria.setPadre(referencias.categoria(request.padreId()));
        }
        if (request.activo() != null) {
            categoria.setActivo(request.activo());
        } else if (creando) {
            categoria.setActivo(true);
        }
    }

    private static CategoriaResponse respuesta(Categoria categoria) {
        Categoria padre = categoria.getPadre();
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNombre(),
                padre == null ? null : padre.getId(),
                padre == null ? null : padre.getNombre(),
                categoria.isActivo());
    }

    private static Specification<Categoria> filtrar(String texto, Boolean activo) {
        return (root, query, cb) -> {
            List<Predicate> partes = new ArrayList<>();
            if (Busqueda.presente(texto)) {
                partes.add(cb.like(cb.lower(root.get("nombre")), Busqueda.patron(texto), '\\'));
            }
            if (activo != null) {
                partes.add(cb.equal(root.get("activo"), activo));
            }
            return partes.isEmpty() ? cb.conjunction() : cb.and(partes.toArray(Predicate[]::new));
        };
    }
}
