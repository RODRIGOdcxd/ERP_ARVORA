package com.erparvora.erp_arvora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.erparvora.erp_arvora.model.UnidadMedida;

public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Long>, JpaSpecificationExecutor<UnidadMedida> {
}
