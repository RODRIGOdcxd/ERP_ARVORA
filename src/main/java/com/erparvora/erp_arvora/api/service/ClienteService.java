package com.erparvora.erp_arvora.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.erparvora.erp_arvora.api.Busqueda;
import com.erparvora.erp_arvora.api.dto.ClienteRequest;
import com.erparvora.erp_arvora.api.dto.ClienteResponse;
import com.erparvora.erp_arvora.exception.RecursoNoEncontradoException;
import com.erparvora.erp_arvora.exception.SolicitudInvalidaException;
import com.erparvora.erp_arvora.model.Cliente;
import com.erparvora.erp_arvora.repository.ClienteRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<ClienteResponse> listar(String texto, Boolean activo, Pageable pageable) {
        return repository.findAll(filtrar(texto, activo), pageable).map(ClienteService::respuesta);
    }

    @Transactional(readOnly = true)
    public ClienteResponse obtener(Long id) {
        return respuesta(buscar(id));
    }

    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        Cliente cliente = new Cliente();
        aplicar(cliente, request, true);
        return respuesta(repository.save(cliente));
    }

    @Transactional
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = buscar(id);
        aplicar(cliente, request, false);
        return respuesta(repository.save(cliente));
    }

    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = buscar(id);
        cliente.setActivo(false);
        repository.save(cliente);
    }

    private Cliente buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("el cliente", id));
    }

    private static void aplicar(Cliente cliente, ClienteRequest request, boolean creando) {
        boolean tieneTipo = request.tipoDocumento() != null;
        boolean tieneNumero = request.numeroDocumento() != null && !request.numeroDocumento().isBlank();
        if (tieneTipo != tieneNumero) {
            throw new SolicitudInvalidaException("El tipo y el número de documento van juntos");
        }
        cliente.setTipoDocumento(request.tipoDocumento());
        cliente.setNumeroDocumento(tieneNumero ? request.numeroDocumento().trim() : null);
        cliente.setNombre(request.nombre().trim());
        cliente.setTelefono(vacio(request.telefono()));
        cliente.setEmail(vacio(request.email()));
        cliente.setDireccion(vacio(request.direccion()));
        cliente.setCiudad(vacio(request.ciudad()));
        cliente.setCanalOrigen(request.canalOrigen());
        cliente.setNotas(vacio(request.notas()));
        if (request.activo() != null) {
            cliente.setActivo(request.activo());
        } else if (creando) {
            cliente.setActivo(true);
        }
    }

    private static String vacio(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    private static ClienteResponse respuesta(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getTipoDocumento(),
                cliente.getNumeroDocumento(),
                cliente.getNombre(),
                cliente.getTelefono(),
                cliente.getEmail(),
                cliente.getDireccion(),
                cliente.getCiudad(),
                cliente.getCanalOrigen(),
                cliente.getNotas(),
                cliente.isActivo(),
                cliente.getCreatedAt(),
                cliente.getUpdatedAt());
    }

    private static Specification<Cliente> filtrar(String texto, Boolean activo) {
        return (root, query, cb) -> {
            List<Predicate> partes = new ArrayList<>();
            if (Busqueda.presente(texto)) {
                String patron = Busqueda.patron(texto);
                partes.add(cb.or(
                        cb.like(cb.lower(root.get("nombre")), patron, '\\'),
                        cb.like(cb.lower(root.get("telefono")), patron, '\\'),
                        cb.like(cb.lower(root.get("email")), patron, '\\'),
                        cb.like(cb.lower(root.get("numeroDocumento")), patron, '\\')));
            }
            if (activo != null) {
                partes.add(cb.equal(root.get("activo"), activo));
            }
            return partes.isEmpty() ? cb.conjunction() : cb.and(partes.toArray(Predicate[]::new));
        };
    }
}
