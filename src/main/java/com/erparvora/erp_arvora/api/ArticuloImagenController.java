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

import com.erparvora.erp_arvora.api.dto.ArticuloImagenRequest;
import com.erparvora.erp_arvora.api.dto.ArticuloImagenResponse;
import com.erparvora.erp_arvora.api.service.ArticuloImagenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/articulos/{articuloId}/imagenes")
@Tag(name = "Artículos", description = "Fotos del artículo. Solo una puede ser la principal.")
public class ArticuloImagenController {

    private final ArticuloImagenService service;

    public ArticuloImagenController(ArticuloImagenService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar fotos de un artículo")
    public List<ArticuloImagenResponse> listar(@PathVariable Long articuloId) {
        return service.listar(articuloId);
    }

    @PostMapping
    @Operation(summary = "Agregar una foto")
    public ResponseEntity<ArticuloImagenResponse> crear(
            @PathVariable Long articuloId, @Valid @RequestBody ArticuloImagenRequest request) {
        ArticuloImagenResponse creada = service.crear(articuloId, request);
        return ResponseEntity.created(URI.create("/api/v1/articulos/" + articuloId + "/imagenes/" + creada.id()))
                .body(creada);
    }

    @PutMapping("/{imagenId}")
    @Operation(summary = "Editar una foto")
    public ArticuloImagenResponse actualizar(
            @PathVariable Long articuloId, @PathVariable Long imagenId, @Valid @RequestBody ArticuloImagenRequest request) {
        return service.actualizar(articuloId, imagenId, request);
    }

    @DeleteMapping("/{imagenId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Quitar una foto")
    public void eliminar(@PathVariable Long articuloId, @PathVariable Long imagenId) {
        service.eliminar(articuloId, imagenId);
    }
}
