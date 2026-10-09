package com.erparvora.erp_arvora.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erparvora.erp_arvora.dto.CategoriaDTO;
import com.erparvora.erp_arvora.model.Categoria;
import com.erparvora.erp_arvora.repository.CategoriaRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarCategorias() {
        return repository.findAllByOrderByNombreAsc().stream()
                .map(CategoriaDTO::from)
                .toList();
    }

    @Transactional
    public CategoriaDTO guardarCategoria(CategoriaDTO categoriaDTO) {
        if (categoriaDTO.nombre() == null || categoriaDTO.nombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es requerido");
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(categoriaDTO.nombre().trim());
        categoria.setActivo(categoriaDTO.activo() == null || categoriaDTO.activo());
        if (categoriaDTO.padreId() != null) {
            Categoria padre = repository.findById(categoriaDTO.padreId())
                    .orElseThrow(() -> new IllegalArgumentException("La categoría padre no existe"));
            categoria.setPadre(padre);
        }

        return CategoriaDTO.from(repository.save(categoria));
    }
}
