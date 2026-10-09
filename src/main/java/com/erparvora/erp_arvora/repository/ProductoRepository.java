package com.erparvora.erp_arvora.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.erparvora.erp_arvora.model.Articulo;
import com.erparvora.erp_arvora.model.TipoArticulo;

public interface ProductoRepository extends JpaRepository<Articulo, Long> {

    @EntityGraph(attributePaths = {"categoria", "unidadMedida"})
    List<Articulo> findByTipoOrderByNombreAsc(TipoArticulo tipo);
}
