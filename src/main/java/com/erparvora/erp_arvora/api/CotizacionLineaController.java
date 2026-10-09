package com.erparvora.erp_arvora.api;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.erparvora.erp_arvora.api.dto.CotizacionLineaRequest;
import com.erparvora.erp_arvora.api.dto.CotizacionLineaResponse;
import com.erparvora.erp_arvora.api.service.CotizacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/cotizaciones/{cotizacionId}/lineas")
@Tag(name = "Cotizaciones", description = "Líneas de la cotización. Solo se modifican en BORRADOR. El importe y los totales los calcula el servidor.")
public class CotizacionLineaController {

    private final CotizacionService service;

    public CotizacionLineaController(CotizacionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar líneas")
    public List<CotizacionLineaResponse> listar(@PathVariable Long cotizacionId) {
        return service.listarLineas(cotizacionId);
    }

    @PostMapping
    @Operation(summary = "Agregar una línea")
    public ResponseEntity<CotizacionLineaResponse> crear(
            @PathVariable Long cotizacionId, @Valid @RequestBody CotizacionLineaRequest request) {
        CotizacionLineaResponse creada = service.crearLinea(cotizacionId, request);
        return ResponseEntity.created(URI.create("/api/v1/cotizaciones/" + cotizacionId + "/lineas/" + creada.id()))
                .body(creada);
    }

    @PutMapping("/{lineaId}")
    @Operation(summary = "Editar una línea")
    public CotizacionLineaResponse actualizar(
            @PathVariable Long cotizacionId, @PathVariable Long lineaId, @Valid @RequestBody CotizacionLineaRequest request) {
        return service.actualizarLinea(cotizacionId, lineaId, request);
    }

    @DeleteMapping("/{lineaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Quitar una línea")
    public void eliminar(@PathVariable Long cotizacionId, @PathVariable Long lineaId) {
        service.eliminarLinea(cotizacionId, lineaId);
    }
}
