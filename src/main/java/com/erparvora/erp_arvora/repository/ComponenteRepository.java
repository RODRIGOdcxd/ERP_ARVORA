package com.erparvora.erp_arvora.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erparvora.erp_arvora.model.Componente;

public interface ComponenteRepository extends JpaRepository<Componente, Long> {

    List<Componente> findByProductoIdOrderByOrdenAscIdAsc(Long productoId);

    Optional<Componente> findByIdAndProductoId(Long id, Long productoId);
}
