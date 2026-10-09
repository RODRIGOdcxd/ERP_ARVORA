package com.erparvora.erp_arvora.api;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.erparvora.erp_arvora.api.dto.UnidadMedidaRequest;
import com.erparvora.erp_arvora.api.dto.UnidadMedidaResponse;
import com.erparvora.erp_arvora.api.service.UnidadMedidaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/unidades-medida")
@Tag(name = "Unidades de medida", description = "UND, M, KG y el resto de unidades de stock.")
public class UnidadMedidaController {

    private final UnidadMedidaService service;

    public UnidadMedidaController(UnidadMedidaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar unidades de medida")
    public Page<UnidadMedidaResponse> listar(
            @RequestParam(required = false) String texto,
            @PageableDefault(size = 20, sort = "codigo") Pageable pageable) {
        return service.listar(texto, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver una unidad de medida")
    public UnidadMedidaResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crear una unidad de medida")
    public ResponseEntity<UnidadMedidaResponse> crear(@Valid @RequestBody UnidadMedidaRequest request) {
        UnidadMedidaResponse creada = service.crear(request);
        return ResponseEntity.created(URI.create("/api/v1/unidades-medida/" + creada.id())).body(creada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar una unidad de medida")
    public UnidadMedidaResponse actualizar(@PathVariable Long id, @Valid @RequestBody UnidadMedidaRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar una unidad que no esté en uso")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
