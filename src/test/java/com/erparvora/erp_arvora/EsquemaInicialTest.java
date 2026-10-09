package com.erparvora.erp_arvora;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import com.erparvora.erp_arvora.model.Cliente;
import com.erparvora.erp_arvora.model.Cotizacion;
import com.erparvora.erp_arvora.model.CotizacionLinea;
import com.erparvora.erp_arvora.model.Moneda;
import com.erparvora.erp_arvora.model.Rol;
import com.erparvora.erp_arvora.model.Usuario;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.erparvora.erp_arvora.model.Existencia;
import com.erparvora.erp_arvora.model.ExistenciaId;
import com.erparvora.erp_arvora.repository.ExistenciaRepository;

@Testcontainers
@SpringBootTest
class EsquemaInicialTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private ExistenciaRepository existenciaRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void contextLoads() {
        Long aplicadas = jdbc.queryForObject(
                "select count(*) from flyway_schema_history where success", Long.class);
        assertThat(aplicadas).isEqualTo(1L);
    }

    @Test
    void siembraElTallerYLosCatalogosBasicos() {
        String almacen = jdbc.queryForObject(
                "select nombre from almacen where codigo = 'TALLER'", String.class);
        assertThat(almacen).isEqualTo("Taller Villa El Salvador");
        assertThat(contar("rol")).isEqualTo(3L);
        assertThat(contar("unidad_medida")).isEqualTo(8L);
        assertThat(contar("proveedor")).isZero();
    }

    @Test
    void laVistaDeExistenciaSumaEntradasYSalidas() {
        Fixture fixture = fixture();
        jdbc.update("""
                insert into movimiento_inventario
                    (tipo, articulo_id, almacen_id, cantidad, costo_unitario, usuario_id)
                values ('COMPRA', ?, ?, 10, 25.5000, ?)
                """, fixture.articuloId(), fixture.almacenId(), fixture.usuarioId());
        jdbc.update("""
                insert into movimiento_inventario
                    (tipo, articulo_id, almacen_id, cantidad, usuario_id)
                values ('VENTA', ?, ?, -3.5, ?)
                """, fixture.articuloId(), fixture.almacenId(), fixture.usuarioId());

        Existencia existencia = existenciaRepository
                .findById(new ExistenciaId(fixture.articuloId(), fixture.almacenId()))
                .orElseThrow();

        assertThat(existencia.getCantidad()).isEqualByComparingTo(new BigDecimal("6.5000"));
    }

    @Test
    void unaCompraNoPuedeRestarStock() {
        Fixture fixture = fixture();
        assertThatThrownBy(() -> jdbc.update("""
                insert into movimiento_inventario
                    (tipo, articulo_id, almacen_id, cantidad, costo_unitario, usuario_id)
                values ('COMPRA', ?, ?, -1, 10, ?)
                """, fixture.articuloId(), fixture.almacenId(), fixture.usuarioId()))
                .isInstanceOf(DataIntegrityViolationException.class)
                .rootCause()
                .hasMessageContaining("ck_mov_signo");
    }

    @Test
    void unaVentaNoPuedeSumarStock() {
        Fixture fixture = fixture();
        assertThatThrownBy(() -> jdbc.update("""
                insert into movimiento_inventario
                    (tipo, articulo_id, almacen_id, cantidad, usuario_id)
                values ('VENTA', ?, ?, 1, ?)
                """, fixture.articuloId(), fixture.almacenId(), fixture.usuarioId()))
                .isInstanceOf(DataIntegrityViolationException.class)
                .rootCause()
                .hasMessageContaining("ck_mov_signo");
    }

    @Test
    void unaCompraExigeCostoYUnTipoAjenoAlCatalogoSeRechaza() {
        Fixture fixture = fixture();
        assertThatThrownBy(() -> jdbc.update("""
                insert into movimiento_inventario
                    (tipo, articulo_id, almacen_id, cantidad, usuario_id)
                values ('COMPRA', ?, ?, 1, ?)
                """, fixture.articuloId(), fixture.almacenId(), fixture.usuarioId()))
                .isInstanceOf(DataIntegrityViolationException.class)
                .rootCause()
                .hasMessageContaining("ck_mov_compra_costo");

        assertThatThrownBy(() -> jdbc.update("""
                insert into movimiento_inventario
                    (tipo, articulo_id, almacen_id, cantidad, costo_unitario, usuario_id)
                values ('RESERVA', ?, ?, 1, 10, ?)
                """, fixture.articuloId(), fixture.almacenId(), fixture.usuarioId()))
                .isInstanceOf(DataIntegrityViolationException.class)
                .rootCause()
                .hasMessageContaining("check constraint");
    }

    @Test
    @Transactional
    void cotizacionConPreciosQueIncluyenIgvGuardaElSubtotalSinImpuesto() {
        Rol rol = entityManager.createQuery("select r from Rol r where r.codigo = 'ADMIN'", Rol.class)
                .getSingleResult();
        Usuario usuario = new Usuario();
        usuario.setEmail("cot-" + UUID.randomUUID() + "@arvora.test");
        usuario.setNombre("Cotizador");
        usuario.setRol(rol);
        entityManager.persist(usuario);

        Cliente cliente = new Cliente();
        cliente.setNombre("Cliente mesa");
        entityManager.persist(cliente);

        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setNumero("COT-" + UUID.randomUUID().toString().substring(0, 12));
        cotizacion.setCliente(cliente);
        cotizacion.setUsuario(usuario);

        CotizacionLinea linea = new CotizacionLinea();
        linea.setCotizacion(cotizacion);
        linea.setLinea((short) 1);
        linea.setDescripcion("Mesa 1.20");
        linea.setCantidad(new BigDecimal("2.0000"));
        linea.setPrecioUnitario(new BigDecimal("59.00"));
        linea.recalcularImporte();
        cotizacion.recalcularTotalesDesdeLineas(List.of(linea));

        entityManager.persist(cotizacion);
        entityManager.persist(linea);
        entityManager.flush();
        entityManager.clear();

        Cotizacion guardada = entityManager.find(Cotizacion.class, cotizacion.getId());
        assertThat(guardada.isPreciosIncluyenIgv()).isTrue();
        assertThat(guardada.getMoneda()).isEqualTo(Moneda.PEN);
        assertThat(guardada.getTasaIgv()).isEqualByComparingTo("0.1800");
        assertThat(guardada.getSubtotal()).isEqualByComparingTo("100.00");
        assertThat(guardada.getIgv()).isEqualByComparingTo("18.00");
        assertThat(guardada.getTotal()).isEqualByComparingTo("118.00");
    }

    private long contar(String tabla) {
        Long total = jdbc.queryForObject("select count(*) from " + tabla, Long.class);
        return total == null ? 0L : total;
    }

    private Fixture fixture() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Long rolId = jdbc.queryForObject("select id from rol where codigo = 'ADMIN'", Long.class);
        Long unidadId = jdbc.queryForObject("select id from unidad_medida where codigo = 'UND'", Long.class);
        Long almacenId = jdbc.queryForObject("select id from almacen where codigo = 'TALLER'", Long.class);
        Long usuarioId = jdbc.queryForObject(
                "insert into usuario (email, nombre, rol_id) values (?, 'Prueba', ?) returning id",
                Long.class, "prueba-" + sufijo + "@arvora.test", rolId);
        Long articuloId = jdbc.queryForObject("""
                insert into articulo (codigo, nombre, tipo, unidad_medida_id, precio_venta)
                values (?, 'Tubo de prueba', 'PRODUCTO', ?, 118.00)
                returning id
                """, Long.class, "TST-" + sufijo, unidadId);
        return new Fixture(usuarioId, articuloId, almacenId);
    }

    private record Fixture(Long usuarioId, Long articuloId, Long almacenId) {
    }
}
