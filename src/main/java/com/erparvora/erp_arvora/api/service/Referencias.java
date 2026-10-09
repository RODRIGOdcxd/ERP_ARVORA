package com.erparvora.erp_arvora.api.service;

import org.springframework.stereotype.Service;

import com.erparvora.erp_arvora.exception.RecursoNoEncontradoException;
import com.erparvora.erp_arvora.model.Almacen;
import com.erparvora.erp_arvora.model.Articulo;
import com.erparvora.erp_arvora.model.Categoria;
import com.erparvora.erp_arvora.model.Cliente;
import com.erparvora.erp_arvora.model.Cotizacion;
import com.erparvora.erp_arvora.model.Proveedor;
import com.erparvora.erp_arvora.model.Rol;
import com.erparvora.erp_arvora.model.UnidadMedida;
import com.erparvora.erp_arvora.model.Usuario;
import com.erparvora.erp_arvora.repository.AlmacenRepository;
import com.erparvora.erp_arvora.repository.ArticuloRepository;
import com.erparvora.erp_arvora.repository.CategoriaRepository;
import com.erparvora.erp_arvora.repository.ClienteRepository;
import com.erparvora.erp_arvora.repository.CotizacionRepository;
import com.erparvora.erp_arvora.repository.ProveedorRepository;
import com.erparvora.erp_arvora.repository.RolRepository;
import com.erparvora.erp_arvora.repository.UnidadMedidaRepository;
import com.erparvora.erp_arvora.repository.UsuarioRepository;

@Service
public class Referencias {

    private final RolRepository roles;
    private final UsuarioRepository usuarios;
    private final ClienteRepository clientes;
    private final ProveedorRepository proveedores;
    private final UnidadMedidaRepository unidades;
    private final CategoriaRepository categorias;
    private final AlmacenRepository almacenes;
    private final ArticuloRepository articulos;
    private final CotizacionRepository cotizaciones;

    public Referencias(RolRepository roles, UsuarioRepository usuarios, ClienteRepository clientes,
            ProveedorRepository proveedores, UnidadMedidaRepository unidades, CategoriaRepository categorias,
            AlmacenRepository almacenes, ArticuloRepository articulos, CotizacionRepository cotizaciones) {
        this.roles = roles;
        this.usuarios = usuarios;
        this.clientes = clientes;
        this.proveedores = proveedores;
        this.unidades = unidades;
        this.categorias = categorias;
        this.almacenes = almacenes;
        this.articulos = articulos;
        this.cotizaciones = cotizaciones;
    }

    public Rol rol(Long id) {
        return roles.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("el rol", id));
    }

    public Usuario usuario(Long id) {
        return usuarios.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("el usuario", id));
    }

    public Cliente cliente(Long id) {
        return clientes.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("el cliente", id));
    }

    public Proveedor proveedor(Long id) {
        return proveedores.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("el proveedor", id));
    }

    public UnidadMedida unidad(Long id) {
        return unidades.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("la unidad de medida", id));
    }

    public Categoria categoria(Long id) {
        return categorias.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("la categoría", id));
    }

    public Almacen almacen(Long id) {
        return almacenes.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("el almacén", id));
    }

    public Articulo articulo(Long id) {
        return articulos.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("el artículo", id));
    }

    public Cotizacion cotizacion(Long id) {
        return cotizaciones.findById(id).orElseThrow(() -> RecursoNoEncontradoException.de("la cotización", id));
    }
}
