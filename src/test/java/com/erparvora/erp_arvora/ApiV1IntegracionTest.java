package com.erparvora.erp_arvora;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import com.jayway.jsonpath.JsonPath;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class ApiV1IntegracionTest {

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
    private MockMvc mvc;

    @Autowired
    private JdbcTemplate jdbc;

    private Long rolAdminId;
    private Long unidadId;
    private Long almacenId;

    @BeforeEach
    void catalogos() {
        rolAdminId = jdbc.queryForObject("select id from rol where codigo = 'ADMIN'", Long.class);
        unidadId = jdbc.queryForObject("select id from unidad_medida where codigo = 'UND'", Long.class);
        almacenId = jdbc.queryForObject("select id from almacen where codigo = 'TALLER'", Long.class);
    }

    @Test
    void validacionDevuelveProblemDetail() throws Exception {
        mvc.perform(post("/api/v1/articulos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Datos inválidos"))
                .andExpect(jsonPath("$.codigo").value("datos_invalidos"))
                .andExpect(jsonPath("$.errores").isArray())
                .andExpect(jsonPath("$.errores[0].campo").exists())
                .andExpect(jsonPath("$.errores[0].mensaje").exists());
    }

    @Test
    void codigoDuplicadoDevuelve409() throws Exception {
        String codigo = "DUP-" + sufijo();
        crearArticulo(codigo, "59.00", null);
        mvc.perform(post("/api/v1/articulos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articuloJson(codigo, "59.00", null)))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Conflicto"))
                .andExpect(jsonPath("$.codigo").value("duplicado"))
                .andExpect(jsonPath("$.detail").value("Ya existe un artículo con ese código"));
    }

    @Test
    void articuloCrudYDesactivacion() throws Exception {
        String codigo = "MESA-" + sufijo();
        String creado = crearArticulo(codigo, "59.00", "40");
        long id = JsonPath.read(creado, "$.id");
        Number version = JsonPath.read(creado, "$.version");

        mvc.perform(get("/api/v1/articulos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(codigo))
                .andExpect(jsonPath("$.preciosIncluyenIgv").value(true))
                .andExpect(jsonPath("$.password").doesNotExist());

        mvc.perform(get("/api/v1/articulos").param("texto", codigo).param("tipo", "PRODUCTO").param("activo", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(id));

        String editado = mvc.perform(put("/api/v1/articulos/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articuloJson(codigo, "70.00", "40").replace("}", ",\"version\":" + version + "}")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(new BigDecimal(JsonPath.read(editado, "$.precioVenta").toString())).isEqualByComparingTo("70.00");

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/v1/articulos/" + id))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/articulos/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    void movimientoSumaEnLaExistenciaYRechazaElSigno() throws Exception {
        long usuarioId = crearUsuario();
        String creado = crearArticulo("STK-" + sufijo(), "10.00", "8");
        long articuloId = JsonPath.read(creado, "$.id");

        mvc.perform(post("/api/v1/movimientos-inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo":"COMPRA","articuloId":%d,"almacenId":%d,"cantidad":10,"costoUnitario":25,"usuarioId":%d}
                                """.formatted(articuloId, almacenId, usuarioId)))
                .andExpect(status().isCreated());

        assertThat(existencia(articuloId)).isEqualByComparingTo("10");

        mvc.perform(post("/api/v1/movimientos-inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo":"VENTA","articuloId":%d,"almacenId":%d,"cantidad":-4,"usuarioId":%d}
                                """.formatted(articuloId, almacenId, usuarioId)))
                .andExpect(status().isCreated());

        assertThat(existencia(articuloId)).isEqualByComparingTo("6");

        mvc.perform(post("/api/v1/movimientos-inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo":"COMPRA","articuloId":%d,"almacenId":%d,"cantidad":-1,"costoUnitario":10,"usuarioId":%d}
                                """.formatted(articuloId, almacenId, usuarioId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("datos_invalidos"))
                .andExpect(jsonPath("$.detail").value("Este tipo de movimiento solo puede sumar stock"));
    }

    @Test
    void cotizacionTomaElPrecioDelArticuloYCalculaElSubtotalSinIgv() throws Exception {
        long usuarioId = crearUsuario();
        long clienteId = crearCliente();
        String articulo = crearArticulo("COT-" + sufijo(), "59.00", "40");
        long articuloId = JsonPath.read(articulo, "$.id");

        String cabecera = mvc.perform(post("/api/v1/cotizaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"clienteId":%d,"usuarioId":%d}
                                """.formatted(clienteId, usuarioId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("BORRADOR"))
                .andExpect(jsonPath("$.preciosIncluyenIgv").value(true))
                .andReturn().getResponse().getContentAsString();
        long cotizacionId = JsonPath.read(cabecera, "$.id");

        mvc.perform(post("/api/v1/cotizaciones/" + cotizacionId + "/lineas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"linea":1,"articuloId":%d,"cantidad":2}
                                """.formatted(articuloId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.precioUnitario").value(59.00))
                .andExpect(jsonPath("$.costoUnitarioEst").value(40))

        String detalle = mvc.perform(get("/api/v1/cotizaciones/" + cotizacionId))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(new BigDecimal(JsonPath.read(detalle, "$.subtotal").toString())).isEqualByComparingTo("100.00");
        assertThat(new BigDecimal(JsonPath.read(detalle, "$.igv").toString())).isEqualByComparingTo("18.00");
        assertThat(new BigDecimal(JsonPath.read(detalle, "$.total").toString())).isEqualByComparingTo("118.00");
        assertThat(new BigDecimal(JsonPath.read(detalle, "$.lineas[0].precioUnitario").toString())).isEqualByComparingTo("59.00");
        assertThat(new BigDecimal(JsonPath.read(detalle, "$.lineas[0].costoUnitarioEst").toString())).isEqualByComparingTo("40");

        Number version = JsonPath.read(detalle, "$.version");
        long lineaId = JsonPath.read(detalle, "$.lineas[0].id");
        mvc.perform(put("/api/v1/cotizaciones/" + cotizacionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"clienteId":%d,"usuarioId":%d,"estado":"ENVIADA","version":%s}
                                """.formatted(clienteId, usuarioId, version)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENVIADA"));

        mvc.perform(put("/api/v1/cotizaciones/" + cotizacionId + "/lineas/" + lineaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"linea":1,"articuloId":%d,"cantidad":3}
                                """.formatted(articuloId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("conflicto"))
                .andExpect(jsonPath("$.detail").value("Las líneas solo se editan mientras la cotización está en borrador"));
    }

    @Test
    void laContrasenaSeGuardaCifradaYNoSeDevuelve() throws Exception {
        String email = "user-" + sufijo() + "@arvora.test";
        String body = mvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","nombre":"Ana","password":"clave-segura","rolId":%d}
                                """.formatted(email, rolAdminId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        long id = JsonPath.read(body, "$.id");
        String hash = jdbc.queryForObject("select password_hash from usuario where id = ?", String.class, id);
        assertThat(hash).startsWith("$2").doesNotContain("clave-segura");
    }

    @Test
    void openApiListaLosRecursos() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/articulos']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/movimientos-inventario']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/existencias']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/cotizaciones/{id}/lineas']").exists());
        mvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    private String crearArticulo(String codigo, String precio, String costo) throws Exception {
        MvcResult result = mvc.perform(post("/api/v1/articulos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(articuloJson(codigo, precio, costo)))
                .andExpect(status().isCreated())
                .andReturn();
        return result.getResponse().getContentAsString();
    }

    private String articuloJson(String codigo, String precio, String costo) {
        String costoJson = costo == null ? "" : ",\"costoReferencia\":" + costo;
        return """
                {"codigo":"%s","nombre":"Mesa %s","tipo":"PRODUCTO","unidadMedidaId":%d,"tipoCorte":"NINGUNO","seVende":true,"precioVenta":%s,"stockMinimo":0%s}
                """.formatted(codigo, codigo, unidadId, precio, costoJson);
    }

    private long crearUsuario() throws Exception {
        String body = mvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"op-%s@arvora.test","nombre":"Operador","password":"clave-segura","rolId":%d}
                                """.formatted(sufijo(), rolAdminId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private long crearCliente() throws Exception {
        String body = mvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Cliente %s","telefono":"999000111"}
                                """.formatted(sufijo())))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private BigDecimal existencia(long articuloId) throws Exception {
        String body = mvc.perform(get("/api/v1/existencias")
                        .param("articuloId", Long.toString(articuloId))
                        .param("almacenId", almacenId.toString()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return new BigDecimal(JsonPath.read(body, "$.content[0].cantidad").toString());
    }

    private static String sufijo() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
