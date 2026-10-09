package com.erparvora.erp_arvora.api.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erparvora.erp_arvora.api.dto.ArticuloImagenRequest;
import com.erparvora.erp_arvora.api.dto.ArticuloImagenResponse;
import com.erparvora.erp_arvora.exception.RecursoNoEncontradoException;
import com.erparvora.erp_arvora.model.Articulo;
import com.erparvora.erp_arvora.model.ArticuloImagen;
import com.erparvora.erp_arvora.repository.ArticuloImagenRepository;

@Service
public class ArticuloImagenService {

    private final ArticuloImagenRepository repository;
    private final Referencias referencias;

    public ArticuloImagenService(ArticuloImagenRepository repository, Referencias referencias) {
        this.repository = repository;
        this.referencias = referencias;
    }

    @Transactional(readOnly = true)
    public List<ArticuloImagenResponse> listar(Long articuloId) {
        referencias.articulo(articuloId);
        return repository.findByArticuloIdOrderByOrdenAscIdAsc(articuloId).stream()
                .map(ArticuloImagenService::respuesta)
                .toList();
    }

    @Transactional
    public ArticuloImagenResponse crear(Long articuloId, ArticuloImagenRequest request) {
        Articulo articulo = referencias.articulo(articuloId);
        boolean principal = Boolean.TRUE.equals(request.principal());
        if (principal) {
            quitarPrincipal(articuloId);
        }
        ArticuloImagen imagen = new ArticuloImagen();
        imagen.setArticulo(articulo);
        aplicar(imagen, request, principal);
        return respuesta(repository.save(imagen));
    }

    @Transactional
    public ArticuloImagenResponse actualizar(Long articuloId, Long imagenId, ArticuloImagenRequest request) {
        ArticuloImagen imagen = buscar(articuloId, imagenId);
        boolean principal = Boolean.TRUE.equals(request.principal());
        if (principal) {
            quitarPrincipal(articuloId);
        }
        aplicar(imagen, request, principal);
        return respuesta(repository.save(imagen));
    }

    @Transactional
    public void eliminar(Long articuloId, Long imagenId) {
        repository.delete(buscar(articuloId, imagenId));
    }

    private ArticuloImagen buscar(Long articuloId, Long imagenId) {
        referencias.articulo(articuloId);
        return repository.findByIdAndArticuloId(imagenId, articuloId)
                .orElseThrow(() -> RecursoNoEncontradoException.de("la imagen", imagenId));
    }

    private void quitarPrincipal(Long articuloId) {
        for (ArticuloImagen imagen : repository.findByArticuloIdOrderByOrdenAscIdAsc(articuloId)) {
            imagen.setPrincipal(false);
        }
        repository.flush();
    }

    private static void aplicar(ArticuloImagen imagen, ArticuloImagenRequest request, boolean principal) {
        imagen.setUrl(request.url().trim());
        imagen.setOrden(request.orden() == null ? 0 : request.orden().shortValue());
        imagen.setPrincipal(principal);
    }

    private static ArticuloImagenResponse respuesta(ArticuloImagen imagen) {
        return new ArticuloImagenResponse(
                imagen.getId(),
                imagen.getArticulo().getId(),
                imagen.getUrl(),
                imagen.getOrden(),
                imagen.isPrincipal());
    }
}
