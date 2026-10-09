package com.erparvora.erp_arvora.api.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erparvora.erp_arvora.api.dto.MovimientoRequest;
import com.erparvora.erp_arvora.api.dto.MovimientoResponse;
import com.erparvora.erp_arvora.exception.RecursoNoEncontradoException;
import com.erparvora.erp_arvora.exception.SolicitudInvalidaException;
import com.erparvora.erp_arvora.model.Almacen;
import com.erparvora.erp_arvora.model.Articulo;
import com.erparvora.erp_arvora.model.MovimientoInventario;
import com.erparvora.erp_arvora.model.Proveedor;
import com.erparvora.erp_arvora.model.TipoMovimientoInventario;
import com.erparvora.erp_arvora.model.Usuario;
import com.erparvora.erp_arvora.repository.MovimientoInventarioRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class MovimientoInventarioService {

    private final MovimientoInventarioRepository repository;
    private final Referencias referencias;

    public MovimientoInventarioService(MovimientoInventarioRepository repository, Referencias referencias) {
        this.repository = repository;
        this.referencias = referencias;
    }

    @Transactional(readOnly = true)
    public Page<MovimientoResponse> listar(Long articuloId, Long almacenId, TipoMovimientoInventario tipo,
            LocalDate desde, LocalDate hasta, Pageable pageable) {
        return repository.findAll(filtrar(articuloId, almacenId, tipo, desde, hasta), pageable)
                .map(MovimientoInventarioService::respuesta);
    }

    @Transactional(readOnly = true)
    public MovimientoResponse obtener(Long id) {
        return respuesta(repository.findById(id)
                .orElseThrow(() -> RecursoNoEncontradoException.de("el movimiento", id)));
    }

    /**
     * Alta del libro. No hay edición ni borrado: una corrección es otro movimiento
     * (AJUSTE, o DEVOLUCION si vuelve mercadería).
     */
    @Transactional
    public MovimientoResponse crear(MovimientoRequest request) {
        validar(request.tipo(), request.cantidad(), request.costoUnitario());
        Articulo articulo = referencias.articulo(request.articuloId());
        if (!articulo.isControlaStock()) {
            throw new SolicitudInvalidaException("Este artículo no controla stock");
        }
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setTipo(request.tipo());
        movimiento.setArticulo(articulo);
        movimiento.setAlmacen(referencias.almacen(request.almacenId()));
        movimiento.setCantidad(request.cantidad());
        movimiento.setCostoUnitario(request.costoUnitario());
        movimiento.setProveedor(request.proveedorId() == null ? null : referencias.proveedor(request.proveedorId()));
        movimiento.setCotizacion(request.cotizacionId() == null ? null : referencias.cotizacion(request.cotizacionId()));
        movimiento.setDocumentoRef(request.documentoRef() == null || request.documentoRef().isBlank()
                ? null
                : request.documentoRef().trim());
        movimiento.setUsuario(referencias.usuario(request.usuarioId()));
        movimiento.setNota(request.nota() == null || request.nota().isBlank() ? null : request.nota().trim());
        movimiento.setFecha(request.fecha());
        return respuesta(repository.save(movimiento));
    }

    static void validar(TipoMovimientoInventario tipo, BigDecimal cantidad, BigDecimal costo) {
        if (cantidad == null || cantidad.signum() == 0) {
            throw new SolicitudInvalidaException("La cantidad no puede ser cero");
        }
        boolean entrada = switch (tipo) {
            case SALDO_INICIAL, COMPRA, INGRESO_PRODUCCION -> true;
            case CONSUMO_PRODUCCION, VENTA -> false;
            case AJUSTE, DEVOLUCION -> cantidad.signum() > 0;
        };
        if ((tipo == TipoMovimientoInventario.SALDO_INICIAL
                || tipo == TipoMovimientoInventario.COMPRA
                || tipo == TipoMovimientoInventario.INGRESO_PRODUCCION) && cantidad.signum() < 0) {
            throw new SolicitudInvalidaException("Este tipo de movimiento solo puede sumar stock");
        }
        if ((tipo == TipoMovimientoInventario.CONSUMO_PRODUCCION || tipo == TipoMovimientoInventario.VENTA)
                && cantidad.signum() > 0) {
            throw new SolicitudInvalidaException("Este tipo de movimiento solo puede restar stock");
        }
        if (tipo == TipoMovimientoInventario.COMPRA && costo == null) {
            throw new SolicitudInvalidaException("Una compra debe indicar el costo unitario sin IGV");
        }
        if (!entrada && costo != null && costo.signum() < 0) {
            throw new SolicitudInvalidaException("El costo no puede ser negativo");
        }
    }

    private static MovimientoResponse respuesta(MovimientoInventario movimiento) {
        Articulo articulo = movimiento.getArticulo();
        Almacen almacen = movimiento.getAlmacen();
        Usuario usuario = movimiento.getUsuario();
        Proveedor proveedor = movimiento.getProveedor();
        return new MovimientoResponse(
                movimiento.getId(),
                movimiento.getFecha(),
                movimiento.getTipo(),
                articulo.getId(),
                articulo.getCodigo(),
                articulo.getNombre(),
                almacen.getId(),
                almacen.getNombre(),
                movimiento.getCantidad(),
                movimiento.getCostoUnitario(),
                proveedor == null ? null : proveedor.getId(),
                movimiento.getCotizacion() == null ? null : movimiento.getCotizacion().getId(),
                movimiento.getDocumentoRef(),
                usuario.getId(),
                usuario.getNombre(),
                movimiento.getNota(),
                movimiento.getCreatedAt());
    }

    private static Specification<MovimientoInventario> filtrar(Long articuloId, Long almacenId,
            TipoMovimientoInventario tipo, LocalDate desde, LocalDate hasta) {
        return (root, query, cb) -> {
            List<Predicate> partes = new ArrayList<>();
            if (articuloId != null) {
                partes.add(cb.equal(root.get("articulo").get("id"), articuloId));
            }
            if (almacenId != null) {
                partes.add(cb.equal(root.get("almacen").get("id"), almacenId));
            }
            if (tipo != null) {
                partes.add(cb.equal(root.get("tipo"), tipo));
            }
            if (desde != null) {
                OffsetDateTime inicio = desde.atStartOfDay().atOffset(ZoneOffset.UTC);
                partes.add(cb.greaterThanOrEqualTo(root.get("fecha"), inicio));
            }
            if (hasta != null) {
                OffsetDateTime fin = hasta.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
                partes.add(cb.lessThan(root.get("fecha"), fin));
            }
            return partes.isEmpty() ? cb.conjunction() : cb.and(partes.toArray(Predicate[]::new));
        };
    }
}
