package com.erparvora.erp_arvora.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erparvora.erp_arvora.dto.RolDTO;
import com.erparvora.erp_arvora.model.Rol;
import com.erparvora.erp_arvora.repository.RolRepository;

@Service
public class RolService {

    private final RolRepository repository;

    public RolService(RolRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<RolDTO> listarRoles() {
        return repository.findAll().stream()
                .map(RolDTO::from)
                .toList();
    }

    @Transactional
    public RolDTO guardarRol(RolDTO rolDTO) {
        if (rolDTO.codigo() == null || rolDTO.codigo().trim().isEmpty()) {
            throw new IllegalArgumentException("El código del rol es requerido");
        }
        if (rolDTO.nombre() == null || rolDTO.nombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol es requerido");
        }

        Rol rol = new Rol();
        rol.setCodigo(rolDTO.codigo().trim());
        rol.setNombre(rolDTO.nombre().trim());
        rol.setActivo(rolDTO.activo() == null || rolDTO.activo());

        return RolDTO.from(repository.save(rol));
    }
}
