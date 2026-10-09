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

import com.erparvora.erp_arvora.api.dto.ComponenteRequest;
import com.erparvora.erp_arvora.api.dto.ComponenteResponse;
import com.erparvora.erp_arvora.api.service.ComponenteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/articulos/{articuloId}/componentes")
@Tag(name = "Artículos", description = "Receta del producto: piezas, medidas, tolerancias y consumos.")
public class ComponenteController {

    private final ComponenteService service;

    public ComponenteController(ComponenteService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar la receta de un artículo")
    public List<ComponenteResponse> listar(@PathVariable Long articuloId) {
        return service.listar(articuloId);
    }

    @PostMapping
    @Operation(summary = "Agregar un componente a la receta")
    public ResponseEntity<ComponenteResponse> crear(
            @PathVariable Long articuloId, @Valid @RequestBody ComponenteRequest request) {
        ComponenteResponse creado = service.crear(articuloId, request);
        return ResponseEntity.created(URI.create("/api/v1/articulos/" + articuloId + "/componentes/" + creado.id()))
                .body(creado);
    }

    @PutMapping("/{componenteId}")
    @Operation(summary = "Editar un componente")
    public ComponenteResponse actualizar(
            @PathVariable Long articuloId,
            @PathVariable Long componenteId,
            @Valid @RequestBody ComponenteRequest request) {
        return service.actualizar(articuloId, componenteId, request);
    }

    @DeleteMapping("/{componenteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Quitar un componente de la receta")
    public void eliminar(@PathVariable Long articuloId, @PathVariable Long componenteId) {
        service.eliminar(articuloId, componenteId);
    }
}
