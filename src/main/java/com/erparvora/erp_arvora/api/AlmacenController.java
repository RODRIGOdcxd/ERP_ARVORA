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

import com.erparvora.erp_arvora.api.dto.AlmacenRequest;
import com.erparvora.erp_arvora.api.dto.AlmacenResponse;
import com.erparvora.erp_arvora.api.service.AlmacenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/almacenes")
@Tag(name = "Almacenes", description = "Lugares físicos del stock. Hoy solo está el taller de Villa El Salvador.")
public class AlmacenController {

    private final AlmacenService service;

    public AlmacenController(AlmacenService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar almacenes")
    public Page<AlmacenResponse> listar(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) Boolean activo,
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return service.listar(texto, activo, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver un almacén")
    public AlmacenResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crear un almacén")
    public ResponseEntity<AlmacenResponse> crear(@Valid @RequestBody AlmacenRequest request) {
        AlmacenResponse creado = service.crear(request);
        return ResponseEntity.created(URI.create("/api/v1/almacenes/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar un almacén")
    public AlmacenResponse actualizar(@PathVariable Long id, @Valid @RequestBody AlmacenRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desactivar un almacén")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
