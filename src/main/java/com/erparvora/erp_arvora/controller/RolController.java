package com.erparvora.erp_arvora.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erparvora.erp_arvora.dto.RolDTO;
import com.erparvora.erp_arvora.model.Rol;
import com.erparvora.erp_arvora.service.RolService;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "http://localhost:3000")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public ResponseEntity<List<Rol>> listarRoles() {
        return ResponseEntity.ok(rolService.listarRoles());
    }

    @PostMapping
    public ResponseEntity<Rol> crearRol(@RequestBody RolDTO rolDTO) {
        Rol rolGuardado = rolService.guardarRol(rolDTO);
        return ResponseEntity.ok(rolGuardado);
    }
}
