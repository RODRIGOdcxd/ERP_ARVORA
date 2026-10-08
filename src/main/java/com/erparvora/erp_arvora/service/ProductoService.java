package com.erparvora.erp_arvora.service;
import java.util.List;
import org.springframework.stereotype.Service;
import com.erparvora.erp_arvora.model.Producto;
import com.erparvora.erp_arvora.repository.ProductoRepository;

@Service
public class ProductoService {
    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public List<Producto> listarProductos() {
        return repository.findAll();
    }

    public Producto guardarProducto(Producto producto) {
        return repository.save(producto);
    }
}