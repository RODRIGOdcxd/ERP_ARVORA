package com.erparvora.erp_arvora.api.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erparvora.erp_arvora.api.Busqueda;
import com.erparvora.erp_arvora.api.dto.ArticuloRequest;
import com.erparvora.erp_arvora.api.dto.ArticuloResponse;
import com.erparvora.erp_arvora.exception.ConflictoNegocioException;
import com.erparvora.erp_arvora.exception.RecursoNoEncontradoException;
import com.erparvora.erp_arvora.exception.SolicitudInvalidaException;
import com.erparvora.erp_arvora.model.Articulo;
import com.erparvora.erp_arvora.model.Categoria;
import com.erparvora.erp_arvora.model.Proveedor;
import com.erparvora.erp_arvora.model.TipoArticulo;
import com.erparvora.erp_arvora.model.TipoCorte;
import com.erparvora.erp_arvora.model.UnidadMedida;
import com.erparvora.erp_arvora.repository.ArticuloRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class ArticuloService {

    private final ArticuloRepository repository;
    private final Referencias referencias;

    public ArticuloService(ArticuloRepository repository, Referencias referencias) {
        this.repository = repository;
        this.referencias = referencias;
    }

    @Transactional(readOnly = true)
    public Page<ArticuloResponse> listar(String texto, TipoArticulo tipo, Long categoriaId, Boolean activo, Pageable pageable) {
        return repository.findAll(filtrar(texto, tipo, categoriaId, activo), pageable).map(ArticuloService::respuesta);
    }

    @Transactional(readOnly = true)
    public ArticuloResponse obtener(Long id) {
        return respuesta(buscar(id));
    }

    @Transactional
    public ArticuloResponse crear(ArticuloRequest request) {
        Articulo articulo = new Articulo();
        aplicar(articulo, request, true);
        return respuesta(repository.save(articulo));
    }

    @Transactional
    public ArticuloResponse actualizar(Long id, ArticuloRequest request) {
        Articulo articulo = buscar(id);
        exigirVersion(articulo.getVersion(), request.version());
        aplicar(articulo, request, false);
        return respuesta(repository.save(articulo));
    }

    @Transactional
    public void eliminar(Long id) {
        Articulo articulo = buscar(id);
        articulo.setActivo(false);
        repository.save(articulo);
    }

    private Articulo buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("el artículo", id));
    }

    private void aplicar(Articulo articulo, ArticuloRequest request, boolean creando) {
        articulo.setCodigo(request.codigo().trim());
        articulo.setNombre(request.nombre().trim());
        articulo.setDescripcion(vacio(request.descripcion()));
        articulo.setTipo(request.tipo());
        articulo.setCategoria(request.categoriaId() == null ? null : referencias.categoria(request.categoriaId()));
        articulo.setUnidadMedida(referencias.unidad(request.unidadMedidaId()));
        articulo.setSeCompra(Boolean.TRUE.equals(request.seCompra()));
        articulo.setSeVende(Boolean.TRUE.equals(request.seVende()));
        articulo.setTipoCorte(request.tipoCorte() == null ? TipoCorte.NINGUNO : request.tipoCorte());
        articulo.setRespetaVeta(Boolean.TRUE.equals(request.respetaVeta()));
        articulo.setMarca(vacio(request.marca()));
        articulo.setColorAcabado(vacio(request.colorAcabado()));
        articulo.setProveedorHabitual(request.proveedorHabitualId() == null
                ? null
                : referencias.proveedor(request.proveedorHabitualId()));
        articulo.setCostoReferencia(request.costoReferencia());
        articulo.setPrecioVenta(request.precioVenta());
        articulo.setLargoMm(request.largoMm());
        articulo.setAnchoMm(request.anchoMm());
        articulo.setAltoMm(request.altoMm());
        articulo.setEspesorMm(request.espesorMm());
        articulo.setDiametroMm(request.diametroMm());
        articulo.setStockMinimo(request.stockMinimo() == null ? BigDecimal.ZERO : request.stockMinimo());
        if (request.tipo() == TipoArticulo.SERVICIO) {
            articulo.setControlaStock(false);
        } else if (request.controlaStock() != null) {
            articulo.setControlaStock(request.controlaStock());
        } else if (creando) {
            articulo.setControlaStock(true);
        }
        if (request.activo() != null) {
            articulo.setActivo(request.activo());
        } else if (creando) {
            articulo.setActivo(true);
        }
        validar(articulo);
    }

    private static void validar(Articulo articulo) {
        if (articulo.getTipoCorte() == TipoCorte.LINEAL && articulo.getLargoMm() == null) {
            throw new SolicitudInvalidaException("Un corte lineal requiere el largo en milímetros");
        }
        if (articulo.getTipoCorte() == TipoCorte.PANEL
                && (articulo.getLargoMm() == null || articulo.getAnchoMm() == null || articulo.getEspesorMm() == null)) {
            throw new SolicitudInvalidaException("Un corte de panel requiere largo, ancho y espesor en milímetros");
        }
        if (articulo.getTipo() == TipoArticulo.SERVICIO && articulo.isControlaStock()) {
            throw new SolicitudInvalidaException("Un servicio no controla stock");
        }
    }

    static void exigirVersion(Long actual, Long enviada) {
        if (enviada == null) {
            throw new SolicitudInvalidaException("Debe enviar la versión del registro para actualizar");
        }
        if (!enviada.equals(actual)) {
            throw new ConflictoNegocioException("Otro usuario modificó este registro. Recargue e intente de nuevo.");
        }
    }

    private static String vacio(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    static ArticuloResponse respuesta(Articulo articulo) {
        Categoria categoria = articulo.getCategoria();
        UnidadMedida unidad = articulo.getUnidadMedida();
        Proveedor proveedor = articulo.getProveedorHabitual();
        return new ArticuloResponse(
                articulo.getId(),
                articulo.getCodigo(),
                articulo.getNombre(),
                articulo.getDescripcion(),
                articulo.getTipo(),
                categoria == null ? null : categoria.getId(),
                categoria == null ? null : categoria.getNombre(),
                unidad.getId(),
                unidad.getCodigo(),
                articulo.isSeCompra(),
                articulo.isSeVende(),
                articulo.isControlaStock(),
                articulo.getLargoMm(),
                articulo.getAnchoMm(),
                articulo.getAltoMm(),
                articulo.getEspesorMm(),
                articulo.getDiametroMm(),
                articulo.getTipoCorte(),
                articulo.isRespetaVeta(),
                articulo.getMarca(),
                articulo.getColorAcabado(),
                proveedor == null ? null : proveedor.getId(),
                proveedor == null ? null : proveedor.getRazonSocial(),
                articulo.getCostoReferencia(),
                articulo.getPrecioVenta(),
                true,
                articulo.getStockMinimo(),
                articulo.isActivo(),
                articulo.getVersion(),
                articulo.getCreatedAt(),
                articulo.getUpdatedAt());
    }

    private static Specification<Articulo> filtrar(String texto, TipoArticulo tipo, Long categoriaId, Boolean activo) {
        return (root, query, cb) -> {
            List<Predicate> partes = new ArrayList<>();
            if (Busqueda.presente(texto)) {
                String patron = Busqueda.patron(texto);
                partes.add(cb.or(
                        cb.like(cb.lower(root.get("codigo")), patron, '\\'),
                        cb.like(cb.lower(root.get("nombre")), patron, '\\')));
            }
            if (tipo != null) {
                partes.add(cb.equal(root.get("tipo"), tipo));
            }
            if (categoriaId != null) {
                partes.add(cb.equal(root.get("categoria").get("id"), categoriaId));
            }
            if (activo != null) {
                partes.add(cb.equal(root.get("activo"), activo));
            }
            return partes.isEmpty() ? cb.conjunction() : cb.and(partes.toArray(Predicate[]::new));
        };
    }
}
