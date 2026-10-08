package com.erparvora.erp_arvora.service;

import com.erparvora.erp_arvora.dto.RolDTO;
import com.erparvora.erp_arvora.model.Rol;
import com.erparvora.erp_arvora.repository.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolService {

    private final RolRepository repository;

    public RolService(RolRepository repository) {
        this.repository = repository;
    }

    public List<Rol> listarRoles() {
        return repository.findAll();
    }

    public Rol guardarRol(RolDTO rolDTO) {
        if (rolDTO.getNombre() == null || rolDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol es requerido");
        }

        Rol rol = new Rol();
        rol.setNombre(rolDTO.getNombre().trim());
        rol.setDescripcion(rolDTO.getDescripcion());
        rol.setActivo(true);

        return repository.save(rol);
    }
}
