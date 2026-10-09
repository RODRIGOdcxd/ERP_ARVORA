package com.erparvora.erp_arvora.api;

import java.net.URI;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.erparvora.erp_arvora.api.dto.MovimientoRequest;
import com.erparvora.erp_arvora.api.dto.MovimientoResponse;
import com.erparvora.erp_arvora.api.service.MovimientoInventarioService;
import com.erparvora.erp_arvora.model.TipoMovimientoInventario;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/movimientos-inventario")
@Tag(name = "Movimientos de inventario", description = """
        Libro de entradas y salidas. Solo se crean y se consultan: no se editan ni se borran.
        Para corregir un error se registra otro movimiento de signo contrario (AJUSTE, o DEVOLUCION si vuelve mercadería).
        """)
public class MovimientoInventarioController {

    private final MovimientoInventarioService service;

    public MovimientoInventarioController(MovimientoInventarioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar movimientos")
    public Page<MovimientoResponse> listar(
            @RequestParam(required = false) Long articuloId,
            @RequestParam(required = false) Long almacenId,
            @RequestParam(required = false) TipoMovimientoInventario tipo,
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            @PageableDefault(size = 20, sort = "fecha", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listar(articuloId, almacenId, tipo, desde, hasta, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ver un movimiento")
    public MovimientoResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Registrar un movimiento")
    public ResponseEntity<MovimientoResponse> crear(@Valid @RequestBody MovimientoRequest request) {
        MovimientoResponse creado = service.crear(request);
        return ResponseEntity.created(URI.create("/api/v1/movimientos-inventario/" + creado.id())).body(creado);
    }
}
