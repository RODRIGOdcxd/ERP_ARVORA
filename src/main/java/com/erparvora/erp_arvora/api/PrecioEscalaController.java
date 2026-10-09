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

import com.erparvora.erp_arvora.api.dto.PrecioEscalaRequest;
import com.erparvora.erp_arvora.api.dto.PrecioEscalaResponse;
import com.erparvora.erp_arvora.api.service.PrecioEscalaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/articulos/{articuloId}/precios-escala")
@Tag(name = "Artículos", description = "Precios por volumen. El precio incluye IGV.")
public class PrecioEscalaController {

    private final PrecioEscalaService service;

    public PrecioEscalaController(PrecioEscalaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar precios por volumen")
    public List<PrecioEscalaResponse> listar(@PathVariable Long articuloId) {
        return service.listar(articuloId);
    }

    @PostMapping
    @Operation(summary = "Agregar un precio por volumen")
    public ResponseEntity<PrecioEscalaResponse> crear(
            @PathVariable Long articuloId, @Valid @RequestBody PrecioEscalaRequest request) {
        PrecioEscalaResponse creado = service.crear(articuloId, request);
        return ResponseEntity.created(URI.create("/api/v1/articulos/" + articuloId + "/precios-escala/" + creado.id()))
                .body(creado);
    }

    @PutMapping("/{precioId}")
    @Operation(summary = "Editar un precio por volumen")
    public PrecioEscalaResponse actualizar(
            @PathVariable Long articuloId, @PathVariable Long precioId, @Valid @RequestBody PrecioEscalaRequest request) {
        return service.actualizar(articuloId, precioId, request);
    }

    @DeleteMapping("/{precioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Quitar un precio por volumen")
    public void eliminar(@PathVariable Long articuloId, @PathVariable Long precioId) {
        service.eliminar(articuloId, precioId);
    }
}
