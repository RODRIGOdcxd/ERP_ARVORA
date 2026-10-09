package com.erparvora.erp_arvora.api.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erparvora.erp_arvora.api.dto.ComponenteRequest;
import com.erparvora.erp_arvora.api.dto.ComponenteResponse;
import com.erparvora.erp_arvora.exception.RecursoNoEncontradoException;
import com.erparvora.erp_arvora.exception.SolicitudInvalidaException;
import com.erparvora.erp_arvora.model.Articulo;
import com.erparvora.erp_arvora.model.Componente;
import com.erparvora.erp_arvora.repository.ComponenteRepository;

@Service
public class ComponenteService {

    private final ComponenteRepository repository;
    private final Referencias referencias;

    public ComponenteService(ComponenteRepository repository, Referencias referencias) {
        this.repository = repository;
        this.referencias = referencias;
    }

    @Transactional(readOnly = true)
    public List<ComponenteResponse> listar(Long productoId) {
        referencias.articulo(productoId);
        return repository.findByProductoIdOrderByOrdenAscIdAsc(productoId).stream()
                .map(ComponenteService::respuesta)
                .toList();
    }

    @Transactional
    public ComponenteResponse crear(Long productoId, ComponenteRequest request) {
        Articulo producto = referencias.articulo(productoId);
        Componente componente = new Componente();
        componente.setProducto(producto);
        aplicar(componente, request);
        return respuesta(repository.save(componente));
    }

    @Transactional
    public ComponenteResponse actualizar(Long productoId, Long componenteId, ComponenteRequest request) {
        Componente componente = buscar(productoId, componenteId);
        aplicar(componente, request);
        return respuesta(repository.save(componente));
    }

    @Transactional
    public void eliminar(Long productoId, Long componenteId) {
        repository.delete(buscar(productoId, componenteId));
    }

    private Componente buscar(Long productoId, Long componenteId) {
        referencias.articulo(productoId);
        return repository.findByIdAndProductoId(componenteId, productoId)
                .orElseThrow(() -> RecursoNoEncontradoException.de("el componente", componenteId));
    }

    private void aplicar(Componente componente, ComponenteRequest request) {
        Articulo material = referencias.articulo(request.materialId());
        if (material.getId().equals(componente.getProducto().getId())) {
            throw new SolicitudInvalidaException("El material no puede ser el mismo producto");
        }
        if (request.largoMm() == null && request.cantidad() == null) {
            throw new SolicitudInvalidaException("Indique el largo de la pieza o la cantidad de consumo");
        }
        componente.setMaterial(material);
        componente.setNombrePieza(request.nombrePieza() == null || request.nombrePieza().isBlank()
                ? null
                : request.nombrePieza().trim());
        componente.setCantidadPiezas(request.cantidadPiezas() == null ? 1 : request.cantidadPiezas());
        componente.setLargoMm(request.largoMm());
        componente.setAnchoMm(request.anchoMm());
        componente.setToleranciaMenosMm(nuloCero(request.toleranciaMenosMm()));
        componente.setToleranciaMasMm(nuloCero(request.toleranciaMasMm()));
        componente.setCantidad(request.cantidad());
        componente.setMermaPct(request.mermaPct() == null ? BigDecimal.ZERO : request.mermaPct());
        componente.setOrden(request.orden() == null ? 0 : request.orden().shortValue());
        componente.setNotas(request.notas() == null || request.notas().isBlank() ? null : request.notas().trim());
    }

    private static BigDecimal nuloCero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private static ComponenteResponse respuesta(Componente componente) {
        Articulo material = componente.getMaterial();
        return new ComponenteResponse(
                componente.getId(),
                componente.getProducto().getId(),
                material.getId(),
                material.getCodigo(),
                material.getNombre(),
                componente.getNombrePieza(),
                componente.getCantidadPiezas(),
                componente.getLargoMm(),
                componente.getAnchoMm(),
                componente.getToleranciaMenosMm(),
                componente.getToleranciaMasMm(),
                componente.getCantidad(),
                componente.getMermaPct(),
                componente.getOrden(),
                componente.getNotas());
    }
}
