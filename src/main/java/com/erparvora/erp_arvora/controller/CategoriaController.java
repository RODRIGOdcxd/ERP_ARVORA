package com.erparvora.erp_arvora.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erparvora.erp_arvora.dto.CategoriaDTO;
import com.erparvora.erp_arvora.model.Categoria;
import com.erparvora.erp_arvora.service.CategoriaService;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "http://localhost:3000")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> listarCategorias() {
        return ResponseEntity.ok(categoriaService.listarCategorias());
    }

    @PostMapping
    public ResponseEntity<Categoria> crearCategoria(@RequestBody CategoriaDTO categoriaDTO) {
        Categoria categoriaGuardada = categoriaService.guardarCategoria(categoriaDTO);
        return ResponseEntity.ok(categoriaGuardada);
    }
}