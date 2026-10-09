package com.erparvora.erp_arvora.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.erparvora.erp_arvora.api.dto.ExistenciaResponse;
import com.erparvora.erp_arvora.api.service.ExistenciaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/existencias")
@Tag(name = "Existencias", description = "Stock calculado con la suma de movimientos. Solo lectura. Filtre por artículo y almacén.")
public class ExistenciaController {

    private final ExistenciaService service;

    public ExistenciaController(ExistenciaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Consultar existencias")
    public Page<ExistenciaResponse> listar(
            @RequestParam(required = false) Long articuloId,
            @RequestParam(required = false) Long almacenId,
            @PageableDefault(size = 20, sort = "cantidad", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listar(articuloId, almacenId, pageable);
    }
}
