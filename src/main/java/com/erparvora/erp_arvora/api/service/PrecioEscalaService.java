package com.erparvora.erp_arvora.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erparvora.erp_arvora.api.dto.PrecioEscalaRequest;
import com.erparvora.erp_arvora.api.dto.PrecioEscalaResponse;
import com.erparvora.erp_arvora.exception.RecursoNoEncontradoException;
import com.erparvora.erp_arvora.model.Articulo;
import com.erparvora.erp_arvora.model.PrecioEscala;
import com.erparvora.erp_arvora.repository.PrecioEscalaRepository;

@Service
public class PrecioEscalaService {

    private final PrecioEscalaRepository repository;
    private final Referencias referencias;

    public PrecioEscalaService(PrecioEscalaRepository repository, Referencias referencias) {
        this.repository = repository;
        this.referencias = referencias;
    }

    @Transactional(readOnly = true)
    public List<PrecioEscalaResponse> listar(Long articuloId) {
        referencias.articulo(articuloId);
        return repository.findByArticuloIdOrderByCantidadMinimaAsc(articuloId).stream()
                .map(PrecioEscalaService::respuesta)
                .toList();
    }

    @Transactional
    public PrecioEscalaResponse crear(Long articuloId, PrecioEscalaRequest request) {
        Articulo articulo = referencias.articulo(articuloId);
        PrecioEscala precio = new PrecioEscala();
        precio.setArticulo(articulo);
        aplicar(precio, request);
        return respuesta(repository.save(precio));
    }

    @Transactional
    public PrecioEscalaResponse actualizar(Long articuloId, Long precioId, PrecioEscalaRequest request) {
        PrecioEscala precio = buscar(articuloId, precioId);
        aplicar(precio, request);
        return respuesta(repository.save(precio));
    }

    @Transactional
    public void eliminar(Long articuloId, Long precioId) {
        repository.delete(buscar(articuloId, precioId));
    }

    private PrecioEscala buscar(Long articuloId, Long precioId) {
        referencias.articulo(articuloId);
        return repository.findByIdAndArticuloId(precioId, articuloId)
                .orElseThrow(() -> RecursoNoEncontradoException.de("el precio por volumen", precioId));
    }

    private static void aplicar(PrecioEscala precio, PrecioEscalaRequest request) {
        precio.setCantidadMinima(request.cantidadMinima());
        precio.setPrecioUnitario(request.precioUnitario());
    }

    private static PrecioEscalaResponse respuesta(PrecioEscala precio) {
        return new PrecioEscalaResponse(
                precio.getId(),
                precio.getArticulo().getId(),
                precio.getCantidadMinima(),
                precio.getPrecioUnitario());
    }
}
