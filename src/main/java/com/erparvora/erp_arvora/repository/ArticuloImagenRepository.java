package com.erparvora.erp_arvora.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erparvora.erp_arvora.model.ArticuloImagen;

public interface ArticuloImagenRepository extends JpaRepository<ArticuloImagen, Long> {

    List<ArticuloImagen> findByArticuloIdOrderByOrdenAscIdAsc(Long articuloId);

    Optional<ArticuloImagen> findByIdAndArticuloId(Long id, Long articuloId);
}
