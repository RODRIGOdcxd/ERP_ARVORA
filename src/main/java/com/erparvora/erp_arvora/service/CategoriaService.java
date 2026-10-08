package com.erparvora.erp_arvora.service;

import org.springframework.stereotype.Service;
import com.erparvora.erp_arvora.dto.CategoriaDTO;
import com.erparvora.erp_arvora.model.Categoria;
import com.erparvora.erp_arvora.repository.CategoriaRepository;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public List<Categoria> listarCategorias() {
        return repository.findAll();
    }

    public Categoria guardarCategoria(CategoriaDTO categoriaDTO) {
        if (categoriaDTO.getNombre() == null || categoriaDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría es requerido");
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(categoriaDTO.getNombre().trim());
        categoria.setDescripcion(categoriaDTO.getDescripcion());
        categoria.setActivo(true);

        return repository.save(categoria);
    }
}
