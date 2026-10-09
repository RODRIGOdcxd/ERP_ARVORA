package com.erparvora.erp_arvora.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.erparvora.erp_arvora.model.Almacen;

public interface AlmacenRepository extends JpaRepository<Almacen, Long>, JpaSpecificationExecutor<Almacen> {
}
