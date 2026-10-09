package com.erparvora.erp_arvora.api.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erparvora.erp_arvora.api.Busqueda;
import com.erparvora.erp_arvora.api.dto.CotizacionLineaRequest;
import com.erparvora.erp_arvora.api.dto.CotizacionLineaResponse;
import com.erparvora.erp_arvora.api.dto.CotizacionRequest;
import com.erparvora.erp_arvora.api.dto.CotizacionResponse;
import com.erparvora.erp_arvora.exception.ConflictoNegocioException;
import com.erparvora.erp_arvora.exception.RecursoNoEncontradoException;
import com.erparvora.erp_arvora.exception.SolicitudInvalidaException;
import com.erparvora.erp_arvora.model.Articulo;
import com.erparvora.erp_arvora.model.Cliente;
import com.erparvora.erp_arvora.model.Cotizacion;
import com.erparvora.erp_arvora.model.CotizacionLinea;
import com.erparvora.erp_arvora.model.EstadoCotizacion;
import com.erparvora.erp_arvora.model.Moneda;
import com.erparvora.erp_arvora.model.Usuario;
import com.erparvora.erp_arvora.repository.CotizacionLineaRepository;
import com.erparvora.erp_arvora.repository.CotizacionRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class CotizacionService {

    private static final EnumSet<EstadoCotizacion> DESDE_ENVIADA = EnumSet.of(
            EstadoCotizacion.ACEPTADA, EstadoCotizacion.RECHAZADA, EstadoCotizacion.VENCIDA, EstadoCotizacion.ANULADA);

    private final CotizacionRepository cotizaciones;
    private final CotizacionLineaRepository lineas;
    private final Referencias referencias;

    public CotizacionService(CotizacionRepository cotizaciones, CotizacionLineaRepository lineas, Referencias referencias) {
        this.cotizaciones = cotizaciones;
        this.lineas = lineas;
        this.referencias = referencias;
    }

    @Transactional(readOnly = true)
    public Page<CotizacionResponse> listar(String texto, Long clienteId, EstadoCotizacion estado, Pageable pageable) {
        Page<Cotizacion> pagina = cotizaciones.findAll(filtrar(texto, clienteId, estado), pageable);
        List<Long> ids = pagina.getContent().stream().map(Cotizacion::getId).toList();
        Map<Long, List<CotizacionLinea>> porCotizacion = ids.isEmpty()
                ? Map.of()
                : lineas.findByCotizacionIdIn(ids).stream()
                        .collect(Collectors.groupingBy(linea -> linea.getCotizacion().getId()));
        return pagina.map(cotizacion -> respuesta(cotizacion, porCotizacion.getOrDefault(cotizacion.getId(), List.of())));
    }

    @Transactional(readOnly = true)
    public CotizacionResponse obtener(Long id) {
        Cotizacion cotizacion = buscar(id);
        return respuesta(cotizacion, lineas.findByCotizacionIdOrderByLineaAsc(id));
    }

    @Transactional
    public CotizacionResponse crear(CotizacionRequest request) {
        Cotizacion cotizacion = new Cotizacion();
        aplicarCabecera(cotizacion, request, true);
        cotizacion.setEstado(EstadoCotizacion.BORRADOR);
        cotizacion.setNumero(request.numero() == null || request.numero().isBlank()
                ? generarNumero(cotizacion.getFechaEmision())
                : request.numero().trim());
        cotizaciones.save(cotizacion);
        recalcular(cotizacion);
        return respuesta(cotizacion, List.of());
    }

    @Transactional
    public CotizacionResponse actualizar(Long id, CotizacionRequest request) {
        Cotizacion cotizacion = buscar(id);
        ArticuloService.exigirVersion(cotizacion.getVersion(), request.version());
        if (cotizacion.getEstado() == EstadoCotizacion.ANULADA) {
            throw new ConflictoNegocioException("Una cotización anulada no se puede modificar");
        }
        if (cotizacion.getEstado() != EstadoCotizacion.BORRADOR) {
            if (request.estado() == null) {
                throw new ConflictoNegocioException("Solo se puede cambiar el estado de una cotización que ya no está en borrador");
            }
            validarTransicion(cotizacion.getEstado(), request.estado());
            cotizacion.setEstado(request.estado());
            if (request.notasInternas() != null) {
                cotizacion.setNotasInternas(vacio(request.notasInternas()));
            }
            return respuesta(cotizaciones.save(cotizacion), lineas.findByCotizacionIdOrderByLineaAsc(id));
        }
        aplicarCabecera(cotizacion, request, false);
        if (request.numero() != null && !request.numero().isBlank()) {
            cotizacion.setNumero(request.numero().trim());
        }
        if (request.estado() != null && request.estado() != EstadoCotizacion.BORRADOR) {
            validarTransicion(EstadoCotizacion.BORRADOR, request.estado());
            cotizacion.setEstado(request.estado());
        }
        recalcular(cotizacion);
        return respuesta(cotizacion, lineas.findByCotizacionIdOrderByLineaAsc(id));
    }

    @Transactional
    public void eliminar(Long id) {
        Cotizacion cotizacion = buscar(id);
        cotizacion.setEstado(EstadoCotizacion.ANULADA);
        cotizaciones.save(cotizacion);
    }

    @Transactional(readOnly = true)
    public List<CotizacionLineaResponse> listarLineas(Long cotizacionId) {
        buscar(cotizacionId);
        return lineas.findByCotizacionIdOrderByLineaAsc(cotizacionId).stream()
                .map(CotizacionService::linea)
                .toList();
    }

    @Transactional
    public CotizacionLineaResponse crearLinea(Long cotizacionId, CotizacionLineaRequest request) {
        Cotizacion cotizacion = buscar(cotizacionId);
        exigirBorrador(cotizacion);
        CotizacionLinea linea = new CotizacionLinea();
        linea.setCotizacion(cotizacion);
        linea.setLinea(request.linea().shortValue());
        aplicarLinea(linea, request, true);
        lineas.save(linea);
        recalcular(cotizacion);
        return linea(linea);
    }

    @Transactional
    public CotizacionLineaResponse actualizarLinea(Long cotizacionId, Long lineaId, CotizacionLineaRequest request) {
        Cotizacion cotizacion = buscar(cotizacionId);
        exigirBorrador(cotizacion);
        CotizacionLinea linea = lineas.findByIdAndCotizacionId(lineaId, cotizacionId)
                .orElseThrow(() -> RecursoNoEncontradoException.de("la línea", lineaId));
        linea.setLinea(request.linea().shortValue());
        aplicarLinea(linea, request, false);
        lineas.save(linea);
        recalcular(cotizacion);
        return linea(linea);
    }

    @Transactional
    public void eliminarLinea(Long cotizacionId, Long lineaId) {
        Cotizacion cotizacion = buscar(cotizacionId);
        exigirBorrador(cotizacion);
        CotizacionLinea linea = lineas.findByIdAndCotizacionId(lineaId, cotizacionId)
                .orElseThrow(() -> RecursoNoEncontradoException.de("la línea", lineaId));
        lineas.delete(linea);
        lineas.flush();
        recalcular(cotizacion);
    }

    private Cotizacion buscar(Long id) {
        return cotizaciones.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("la cotización", id));
    }

    private void aplicarCabecera(Cotizacion cotizacion, CotizacionRequest request, boolean creando) {
        cotizacion.setCliente(referencias.cliente(request.clienteId()));
        cotizacion.setUsuario(referencias.usuario(request.usuarioId()));
        LocalDate emision = request.fechaEmision() == null ? LocalDate.now(ZoneOffset.UTC) : request.fechaEmision();
        if (request.validaHasta() != null && request.validaHasta().isBefore(emision)) {
            throw new SolicitudInvalidaException("La validez no puede ser anterior a la fecha de emisión");
        }
        cotizacion.setFechaEmision(emision);
        cotizacion.setValidaHasta(request.validaHasta());
        cotizacion.setMoneda(request.moneda() == null ? Moneda.PEN : request.moneda());
        cotizacion.setPreciosIncluyenIgv(request.preciosIncluyenIgv() == null || request.preciosIncluyenIgv());
        cotizacion.setTasaIgv(request.tasaIgv() == null ? new BigDecimal("0.1800") : request.tasaIgv());
        cotizacion.setDescuento(request.descuento() == null ? new BigDecimal("0.00") : request.descuento());
        cotizacion.setAdelantoPct(request.adelantoPct() == null ? new BigDecimal("50.00") : request.adelantoPct());
        cotizacion.setPlazoEntregaDias(request.plazoEntregaDias() == null ? null : request.plazoEntregaDias().shortValue());
        cotizacion.setCondiciones(vacio(request.condiciones()));
        cotizacion.setNotasInternas(vacio(request.notasInternas()));
        if (!creando && cotizacion.getNumero() == null) {
            cotizacion.setNumero(generarNumero(emision));
        }
    }

    private void aplicarLinea(CotizacionLinea linea, CotizacionLineaRequest request, boolean creacion) {
        if (request.articuloId() != null) {
            Articulo articulo = referencias.articulo(request.articuloId());
            boolean cambio = linea.getArticulo() == null || !linea.getArticulo().getId().equals(articulo.getId());
            linea.setArticulo(articulo);
            if (creacion || cambio) {
                linea.setCostoUnitarioEst(articulo.getCostoReferencia());
            }
            if (request.precioUnitario() != null) {
                linea.setPrecioUnitario(request.precioUnitario());
            } else if (creacion || cambio || linea.getPrecioUnitario() == null) {
                if (articulo.getPrecioVenta() == null) {
                    throw new SolicitudInvalidaException("El artículo no tiene precio de lista; indique el precio unitario");
                }
                linea.setPrecioUnitario(articulo.getPrecioVenta());
            }
            linea.setDescripcion(request.descripcion() == null || request.descripcion().isBlank()
                    ? articulo.getNombre()
                    : request.descripcion().trim());
            linea.setUnidadCodigo(request.unidadCodigo() == null || request.unidadCodigo().isBlank()
                    ? articulo.getUnidadMedida().getCodigo()
                    : request.unidadCodigo().trim());
        } else {
            linea.setArticulo(null);
            linea.setCostoUnitarioEst(null);
            if (request.descripcion() == null || request.descripcion().isBlank()) {
                throw new SolicitudInvalidaException("La descripción es obligatoria para un ítem sin artículo");
            }
            if (request.precioUnitario() == null) {
                throw new SolicitudInvalidaException("El precio unitario es obligatorio para un ítem sin artículo");
            }
            linea.setDescripcion(request.descripcion().trim());
            linea.setPrecioUnitario(request.precioUnitario());
            linea.setUnidadCodigo(request.unidadCodigo() == null || request.unidadCodigo().isBlank()
                    ? "UND"
                    : request.unidadCodigo().trim());
        }
        linea.setCantidad(request.cantidad());
        linea.setDescuento(request.descuento() == null ? BigDecimal.ZERO : request.descuento());
        linea.setLargoMm(request.largoMm());
        linea.setAnchoMm(request.anchoMm());
        linea.setAltoMm(request.altoMm());
        linea.recalcularImporte();
        if (linea.getImporte().signum() < 0) {
            throw new SolicitudInvalidaException("El descuento de la línea no puede superar el importe");
        }
    }

    private void recalcular(Cotizacion cotizacion) {
        List<CotizacionLinea> todas = lineas.findByCotizacionIdOrderByLineaAsc(cotizacion.getId());
        cotizacion.recalcularTotalesDesdeLineas(todas);
        cotizaciones.save(cotizacion);
    }

    private String generarNumero(LocalDate fecha) {
        int anio = (fecha == null ? LocalDate.now(ZoneOffset.UTC) : fecha).getYear();
        String prefijo = "COT-" + anio + "-";
        List<String> ultimos = cotizaciones.numerosConPrefijo(prefijo, PageRequest.of(0, 1));
        int siguiente = 1;
        if (!ultimos.isEmpty() && ultimos.get(0).length() > prefijo.length()) {
            try {
                siguiente = Integer.parseInt(ultimos.get(0).substring(prefijo.length())) + 1;
            } catch (NumberFormatException ex) {
                siguiente = 1;
            }
        }
        if (siguiente > 99999) {
            throw new ConflictoNegocioException("Se agotó la numeración de cotizaciones del año " + anio);
        }
        return prefijo + String.format("%05d", siguiente);
    }

    private static void exigirBorrador(Cotizacion cotizacion) {
        if (cotizacion.getEstado() != EstadoCotizacion.BORRADOR) {
            throw new ConflictoNegocioException("Las líneas solo se editan mientras la cotización está en borrador");
        }
    }

    private static void validarTransicion(EstadoCotizacion desde, EstadoCotizacion hacia) {
        if (desde == hacia) {
            return;
        }
        boolean permitida = switch (desde) {
            case BORRADOR -> hacia == EstadoCotizacion.ENVIADA || hacia == EstadoCotizacion.ANULADA;
            case ENVIADA -> DESDE_ENVIADA.contains(hacia);
            case ACEPTADA, RECHAZADA, VENCIDA -> hacia == EstadoCotizacion.ANULADA;
            case ANULADA -> false;
        };
        if (!permitida) {
            throw new ConflictoNegocioException("No se puede pasar de " + desde + " a " + hacia);
        }
    }

    private static String vacio(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    private static CotizacionResponse respuesta(Cotizacion cotizacion, List<CotizacionLinea> detalle) {
        Cliente cliente = cotizacion.getCliente();
        Usuario usuario = cotizacion.getUsuario();
        return new CotizacionResponse(
                cotizacion.getId(),
                cotizacion.getNumero(),
                cliente.getId(),
                cliente.getNombre(),
                usuario.getId(),
                usuario.getNombre(),
                cotizacion.getFechaEmision(),
                cotizacion.getValidaHasta(),
                cotizacion.getEstado(),
                cotizacion.getMoneda(),
                cotizacion.isPreciosIncluyenIgv(),
                cotizacion.getTasaIgv(),
                cotizacion.getSubtotal(),
                cotizacion.getDescuento(),
                cotizacion.getIgv(),
                cotizacion.getTotal(),
                cotizacion.getAdelantoPct(),
                cotizacion.getPlazoEntregaDias(),
                cotizacion.getCondiciones(),
                cotizacion.getNotasInternas(),
                cotizacion.getVersion(),
                detalle.stream().map(CotizacionService::linea).toList(),
                cotizacion.getCreatedAt(),
                cotizacion.getUpdatedAt());
    }

    private static CotizacionLineaResponse linea(CotizacionLinea linea) {
        return new CotizacionLineaResponse(
                linea.getId(),
                linea.getLinea(),
                linea.getArticulo() == null ? null : linea.getArticulo().getId(),
                linea.getDescripcion(),
                linea.getLargoMm(),
                linea.getAnchoMm(),
                linea.getAltoMm(),
                linea.getCantidad(),
                linea.getUnidadCodigo(),
                linea.getPrecioUnitario(),
                linea.getDescuento(),
                linea.getImporte(),
                linea.getCostoUnitarioEst());
    }

    private static Specification<Cotizacion> filtrar(String texto, Long clienteId, EstadoCotizacion estado) {
        return (root, query, cb) -> {
            List<Predicate> partes = new ArrayList<>();
            if (Busqueda.presente(texto)) {
                partes.add(cb.like(cb.lower(root.get("numero")), Busqueda.patron(texto), '\\'));
            }
            if (clienteId != null) {
                partes.add(cb.equal(root.get("cliente").get("id"), clienteId));
            }
            if (estado != null) {
                partes.add(cb.equal(root.get("estado"), estado));
            }
            return partes.isEmpty() ? cb.conjunction() : cb.and(partes.toArray(Predicate[]::new));
        };
    }
}
