package com.erparvora.erp_arvora.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erparvora.erp_arvora.model.CotizacionLinea;

public interface CotizacionLineaRepository extends JpaRepository<CotizacionLinea, Long> {

    List<CotizacionLinea> findByCotizacionIdOrderByLineaAsc(Long cotizacionId);

    List<CotizacionLinea> findByCotizacionIdIn(Collection<Long> cotizacionIds);

    Optional<CotizacionLinea> findByIdAndCotizacionId(Long id, Long cotizacionId);
}
