package com.erparvora.erp_arvora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.erparvora.erp_arvora.model.Existencia;
import com.erparvora.erp_arvora.model.ExistenciaId;

public interface ExistenciaRepository extends JpaRepository<Existencia, ExistenciaId>, JpaSpecificationExecutor<Existencia> {
}
