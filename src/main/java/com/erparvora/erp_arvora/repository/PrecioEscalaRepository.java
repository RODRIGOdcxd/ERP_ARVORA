package com.erparvora.erp_arvora.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erparvora.erp_arvora.model.PrecioEscala;

public interface PrecioEscalaRepository extends JpaRepository<PrecioEscala, Long> {

    List<PrecioEscala> findByArticuloIdOrderByCantidadMinimaAsc(Long articuloId);

    Optional<PrecioEscala> findByIdAndArticuloId(Long id, Long articuloId);
}
