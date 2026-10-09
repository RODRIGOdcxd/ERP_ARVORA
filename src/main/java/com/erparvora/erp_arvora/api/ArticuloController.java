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

import com.erparvora.erp_arvora.api.dto.ArticuloRequest;
import com.erparvora.erp_arvora.api.dto.ArticuloResponse;
import com.erparvora.erp_arvora.api.service.ArticuloService;
import com.erparvora.erp_arvora.model.TipoArticulo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/articulos")
@Tag(name = "Artículos", description = "Productos, materias primas, insumos, reventa y servicios. El precio de lista incluye IGV.")
public class ArticuloController {

    private final ArticuloService service;

    public ArticuloController(ArticuloService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar artículos")
    public Page<ArticuloResponse> listar(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) TipoArticulo tipo,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) Boolean activo,
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        return service.listar(texto, tipo, categoriaId, activo, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver un artículo")
    public ArticuloResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crear un artículo")
    public ResponseEntity<ArticuloResponse> crear(@Valid @RequestBody ArticuloRequest request) {
        ArticuloResponse creado = service.crear(request);
        return ResponseEntity.created(URI.create("/api/v1/articulos/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar un artículo")
    public ArticuloResponse actualizar(@PathVariable Long id, @Valid @RequestBody ArticuloRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desactivar un artículo")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
