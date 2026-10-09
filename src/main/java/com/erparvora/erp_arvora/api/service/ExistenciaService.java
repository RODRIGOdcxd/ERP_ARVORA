package com.erparvora.erp_arvora.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erparvora.erp_arvora.api.dto.ExistenciaResponse;
import com.erparvora.erp_arvora.model.Almacen;
import com.erparvora.erp_arvora.model.Articulo;
import com.erparvora.erp_arvora.model.Existencia;
import com.erparvora.erp_arvora.repository.AlmacenRepository;
import com.erparvora.erp_arvora.repository.ArticuloRepository;
import com.erparvora.erp_arvora.repository.ExistenciaRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class ExistenciaService {

    private final ExistenciaRepository repository;
    private final ArticuloRepository articulos;
    private final AlmacenRepository almacenes;

    public ExistenciaService(ExistenciaRepository repository, ArticuloRepository articulos, AlmacenRepository almacenes) {
        this.repository = repository;
        this.articulos = articulos;
        this.almacenes = almacenes;
    }

    @Transactional(readOnly = true)
    public Page<ExistenciaResponse> listar(Long articuloId, Long almacenId, Pageable pageable) {
        Page<Existencia> pagina = repository.findAll(filtrar(articuloId, almacenId), pageable);
        Map<Long, Articulo> porArticulo = new HashMap<>();
        Map<Long, Almacen> porAlmacen = new HashMap<>();
        List<Long> articuloIds = pagina.getContent().stream().map(fila -> fila.getId().getArticuloId()).distinct().toList();
        List<Long> almacenIds = pagina.getContent().stream().map(fila -> fila.getId().getAlmacenId()).distinct().toList();
        if (!articuloIds.isEmpty()) {
            articulos.findAllById(articuloIds).forEach(articulo -> porArticulo.put(articulo.getId(), articulo));
        }
        if (!almacenIds.isEmpty()) {
            almacenes.findAllById(almacenIds).forEach(almacen -> porAlmacen.put(almacen.getId(), almacen));
        }
        return pagina.map(fila -> {
            Articulo articulo = porArticulo.get(fila.getId().getArticuloId());
            Almacen almacen = porAlmacen.get(fila.getId().getAlmacenId());
            return new ExistenciaResponse(
                    fila.getId().getArticuloId(),
                    articulo == null ? null : articulo.getCodigo(),
                    articulo == null ? null : articulo.getNombre(),
                    fila.getId().getAlmacenId(),
                    almacen == null ? null : almacen.getCodigo(),
                    almacen == null ? null : almacen.getNombre(),
                    fila.getCantidad());
        });
    }

    private static Specification<Existencia> filtrar(Long articuloId, Long almacenId) {
        return (root, query, cb) -> {
            List<Predicate> partes = new ArrayList<>();
            if (articuloId != null) {
                partes.add(cb.equal(root.get("id").get("articuloId"), articuloId));
            }
            if (almacenId != null) {
                partes.add(cb.equal(root.get("id").get("almacenId"), almacenId));
            }
            return partes.isEmpty() ? cb.conjunction() : cb.and(partes.toArray(Predicate[]::new));
        };
    }
}
