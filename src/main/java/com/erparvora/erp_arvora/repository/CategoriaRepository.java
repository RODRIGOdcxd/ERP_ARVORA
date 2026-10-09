package com.erparvora.erp_arvora.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.erparvora.erp_arvora.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>, JpaSpecificationExecutor<Categoria> {

    @EntityGraph(attributePaths = "padre")
    List<Categoria> findAllByOrderByNombreAsc();
}
