package com.erparvora.erp_arvora.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erparvora.erp_arvora.dto.ProductoDTO;
import com.erparvora.erp_arvora.model.TipoArticulo;
import com.erparvora.erp_arvora.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> listarProductos() {
        return repository.findByTipoOrderByNombreAsc(TipoArticulo.PRODUCTO).stream()
                .map(ProductoDTO::from)
                .toList();
    }
}
