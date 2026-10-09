package com.erparvora.erp_arvora.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.erparvora.erp_arvora.model.Cotizacion;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Long>, JpaSpecificationExecutor<Cotizacion> {

    @Query("""
            select c.numero from Cotizacion c
            where c.numero like concat(:prefijo, '%')
            order by c.numero desc
            """)
    List<String> numerosConPrefijo(@Param("prefijo") String prefijo, Pageable pageable);
}
