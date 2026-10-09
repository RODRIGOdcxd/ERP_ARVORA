package com.erparvora.erp_arvora.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erparvora.erp_arvora.api.Busqueda;
import com.erparvora.erp_arvora.api.dto.ProveedorRequest;
import com.erparvora.erp_arvora.api.dto.ProveedorResponse;
import com.erparvora.erp_arvora.exception.RecursoNoEncontradoException;
import com.erparvora.erp_arvora.model.Proveedor;
import com.erparvora.erp_arvora.repository.ProveedorRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class ProveedorService {

    private final ProveedorRepository repository;

    public ProveedorService(ProveedorRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<ProveedorResponse> listar(String texto, Boolean activo, Pageable pageable) {
        return repository.findAll(filtrar(texto, activo), pageable).map(ProveedorService::respuesta);
    }

    @Transactional(readOnly = true)
    public ProveedorResponse obtener(Long id) {
        return respuesta(buscar(id));
    }

    @Transactional
    public ProveedorResponse crear(ProveedorRequest request) {
        Proveedor proveedor = new Proveedor();
        aplicar(proveedor, request, true);
        return respuesta(repository.save(proveedor));
    }

    @Transactional
    public ProveedorResponse actualizar(Long id, ProveedorRequest request) {
        Proveedor proveedor = buscar(id);
        aplicar(proveedor, request, false);
        return respuesta(repository.save(proveedor));
    }

    @Transactional
    public void eliminar(Long id) {
        Proveedor proveedor = buscar(id);
        proveedor.setActivo(false);
        repository.save(proveedor);
    }

    private Proveedor buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("el proveedor", id));
    }

    private static void aplicar(Proveedor proveedor, ProveedorRequest request, boolean creando) {
        proveedor.setRuc(request.ruc() == null || request.ruc().isBlank() ? null : request.ruc().trim());
        proveedor.setRazonSocial(request.razonSocial().trim());
        proveedor.setNombreComercial(vacio(request.nombreComercial()));
        proveedor.setContacto(vacio(request.contacto()));
        proveedor.setTelefono(vacio(request.telefono()));
        proveedor.setEmail(vacio(request.email()));
        proveedor.setDireccion(vacio(request.direccion()));
        proveedor.setRubro(vacio(request.rubro()));
        proveedor.setNotas(vacio(request.notas()));
        if (request.activo() != null) {
            proveedor.setActivo(request.activo());
        } else if (creando) {
            proveedor.setActivo(true);
        }
    }

    private static String vacio(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    private static ProveedorResponse respuesta(Proveedor proveedor) {
        return new ProveedorResponse(
                proveedor.getId(),
                proveedor.getRuc(),
                proveedor.getRazonSocial(),
                proveedor.getNombreComercial(),
                proveedor.getContacto(),
                proveedor.getTelefono(),
                proveedor.getEmail(),
                proveedor.getDireccion(),
                proveedor.getRubro(),
                proveedor.getNotas(),
                proveedor.isActivo(),
                proveedor.getCreatedAt(),
                proveedor.getUpdatedAt());
    }

    private static Specification<Proveedor> filtrar(String texto, Boolean activo) {
        return (root, query, cb) -> {
            List<Predicate> partes = new ArrayList<>();
            if (Busqueda.presente(texto)) {
                String patron = Busqueda.patron(texto);
                partes.add(cb.or(
                        cb.like(cb.lower(root.get("razonSocial")), patron, '\\'),
                        cb.like(cb.lower(root.get("nombreComercial")), patron, '\\'),
                        cb.like(cb.lower(root.get("ruc")), patron, '\\'),
                        cb.like(cb.lower(root.get("telefono")), patron, '\\')));
            }
            if (activo != null) {
                partes.add(cb.equal(root.get("activo"), activo));
            }
            return partes.isEmpty() ? cb.conjunction() : cb.and(partes.toArray(Predicate[]::new));
        };
    }
}
