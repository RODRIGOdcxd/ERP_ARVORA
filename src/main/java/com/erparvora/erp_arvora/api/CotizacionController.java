package com.erparvora.erp_arvora.api;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.erparvora.erp_arvora.api.dto.CotizacionRequest;
import com.erparvora.erp_arvora.api.dto.CotizacionResponse;
import com.erparvora.erp_arvora.api.service.CotizacionService;
import com.erparvora.erp_arvora.model.EstadoCotizacion;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/cotizaciones")
@Tag(name = "Cotizaciones", description = """
        Cotizaciones al cliente. El subtotal es la base sin IGV y se recalcula en el servidor.
        Por defecto los precios incluyen IGV (18%). Las líneas solo se editan en estado BORRADOR.
        Eliminar anula la cotización; no la borra.
        """)
public class CotizacionController {

    private final CotizacionService service;

    public CotizacionController(CotizacionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar cotizaciones")
    public Page<CotizacionResponse> listar(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) EstadoCotizacion estado,
            @PageableDefault(size = 20, sort = "fechaEmision", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listar(texto, clienteId, estado, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver una cotización con sus líneas")
    public CotizacionResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crear una cotización en borrador")
    public ResponseEntity<CotizacionResponse> crear(@Valid @RequestBody CotizacionRequest request) {
        CotizacionResponse creada = service.crear(request);
        return ResponseEntity.created(URI.create("/api/v1/cotizaciones/" + creada.id())).body(creada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar una cotización o cambiar su estado")
    public CotizacionResponse actualizar(@PathVariable Long id, @Valid @RequestBody CotizacionRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Anular una cotización")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
