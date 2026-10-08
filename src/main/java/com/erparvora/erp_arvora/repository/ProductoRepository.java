package com.erparvora.erp_arvora.repository;

import com.erparvora.erp_arvora.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

// Capa que accede a la base de datos para la entidad Producto
public interface ProductoRepository 
        extends JpaRepository<Producto, Long> {
}