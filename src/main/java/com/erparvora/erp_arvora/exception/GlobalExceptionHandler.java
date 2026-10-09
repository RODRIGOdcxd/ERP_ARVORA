package com.erparvora.erp_arvora.exception;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.erparvora.erp_arvora.api.CampoError;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Pattern CONSTRAINT = Pattern.compile("constraint \"([^\"]+)\"");

    private static final Map<String, String> POR_RESTRICCION = Map.ofEntries(
            Map.entry("rol_codigo_key", "Ya existe un rol con ese código"),
            Map.entry("ux_usuario_email", "Ya existe un usuario con ese correo"),
            Map.entry("ux_categoria_nombre", "Ya existe una categoría con ese nombre"),
            Map.entry("unidad_medida_codigo_key", "Ya existe una unidad de medida con ese código"),
            Map.entry("almacen_codigo_key", "Ya existe un almacén con ese código"),
            Map.entry("articulo_codigo_key", "Ya existe un artículo con ese código"),
            Map.entry("ux_proveedor_ruc", "Ya existe un proveedor con ese RUC"),
            Map.entry("ux_cliente_documento", "Ya existe un cliente con ese documento"),
            Map.entry("cotizacion_numero_key", "Ya existe una cotización con ese número"),
            Map.entry("ux_precio_escala", "Ya existe un precio para esa cantidad mínima"),
            Map.entry("ux_cot_linea", "Ya existe una línea con ese número"),
            Map.entry("ux_articulo_imagen_principal", "El artículo ya tiene una imagen principal"),
            Map.entry("ck_proveedor_ruc", "El RUC debe tener 11 dígitos y empezar por 10, 15, 17 o 20"),
            Map.entry("ck_cliente_doc_par", "El tipo y el número de documento van juntos"),
            Map.entry("ck_cliente_tipo_doc", "Tipo de documento no permitido"),
            Map.entry("ck_mov_signo", "El signo de la cantidad no corresponde al tipo de movimiento"),
            Map.entry("ck_mov_compra_costo", "Una compra debe indicar el costo unitario"),
            Map.entry("ck_mov_cantidad_no_cero", "La cantidad no puede ser cero"),
            Map.entry("ck_mov_tipo", "Tipo de movimiento no permitido"),
            Map.entry("ck_articulo_corte_lineal", "Un corte lineal requiere el largo"),
            Map.entry("ck_articulo_corte_panel", "Un panel requiere largo, ancho y espesor"),
            Map.entry("ck_articulo_servicio_sin_stock", "Un servicio no controla stock"),
            Map.entry("ck_componente_distinto", "El material no puede ser el mismo producto"),
            Map.entry("ck_componente_forma", "Indique el largo de la pieza o la cantidad de consumo"),
            Map.entry("ck_cot_fechas", "La validez no puede ser anterior a la fecha de emisión"),
            Map.entry("ck_categoria_no_self", "Una categoría no puede ser su propio padre"));

    @ExceptionHandler(RecursoNoEncontradoException.class)
    ProblemDetail noEncontrado(RecursoNoEncontradoException ex, HttpServletRequest request) {
        return problema(HttpStatus.NOT_FOUND, "No encontrado", ex.getMessage(), "no_encontrado", request.getRequestURI());
    }

    @ExceptionHandler(SolicitudInvalidaException.class)
    ProblemDetail solicitudInvalida(SolicitudInvalidaException ex, HttpServletRequest request) {
        return problema(HttpStatus.BAD_REQUEST, "Datos inválidos", ex.getMessage(), "datos_invalidos", request.getRequestURI());
    }

    @ExceptionHandler(ConflictoNegocioException.class)
    ProblemDetail conflicto(ConflictoNegocioException ex, HttpServletRequest request) {
        return problema(HttpStatus.CONFLICT, "Conflicto", ex.getMessage(), "conflicto", request.getRequestURI());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    ProblemDetail version(ObjectOptimisticLockingFailureException ex, HttpServletRequest request) {
        return problema(HttpStatus.CONFLICT, "Conflicto",
                "Otro usuario modificó este registro. Recargue e intente de nuevo.",
                "version", request.getRequestURI());
    }

    @ExceptionHandler(PropertyReferenceException.class)
    ProblemDetail ordenInvalido(PropertyReferenceException ex, HttpServletRequest request) {
        return problema(HttpStatus.BAD_REQUEST, "Datos inválidos",
                "No se puede ordenar por '" + ex.getPropertyName() + "'",
                "datos_invalidos", request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail argumento(IllegalArgumentException ex, HttpServletRequest request) {
        String mensaje = ex.getMessage() == null ? "La solicitud no es válida" : ex.getMessage();
        return problema(HttpStatus.BAD_REQUEST, "Datos inválidos", mensaje, "datos_invalidos", request.getRequestURI());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail integridad(DataIntegrityViolationException ex, HttpServletRequest request) {
        String sqlState = sqlState(ex);
        String restriccion = restriccion(ex);
        String conocido = restriccion == null ? null : POR_RESTRICCION.get(restriccion);
        if ("23505".equals(sqlState)) {
            String detalle = conocido == null ? "Ya existe un registro con esos datos" : conocido;
            return problema(HttpStatus.CONFLICT, "Conflicto", detalle, "duplicado", request.getRequestURI());
        }
        if ("23503".equals(sqlState)) {
            return problema(HttpStatus.CONFLICT, "Conflicto",
                    "No se puede completar porque el registro está en uso o la referencia no existe",
                    "en_uso", request.getRequestURI());
        }
        if ("23514".equals(sqlState)) {
            String detalle = conocido == null ? "Los datos no cumplen una regla del sistema" : conocido;
            return problema(HttpStatus.BAD_REQUEST, "Datos inválidos", detalle, "datos_invalidos", request.getRequestURI());
        }
        if ("22001".equals(sqlState)) {
            return problema(HttpStatus.BAD_REQUEST, "Datos inválidos",
                    "Un dato excede el tamaño permitido", "datos_invalidos", request.getRequestURI());
        }
        String detalle = conocido == null ? "No se pudo guardar por un conflicto con los datos existentes" : conocido;
        return problema(HttpStatus.CONFLICT, "Conflicto", detalle, "conflicto", request.getRequestURI());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<CampoError> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new CampoError(error.getField(),
                        error.getDefaultMessage() == null ? "Valor no válido" : error.getDefaultMessage()))
                .toList();
        ProblemDetail problema = problema(HttpStatus.BAD_REQUEST, "Datos inválidos",
                "Revise los campos marcados", "datos_invalidos", ruta(request));
        problema.setProperty("errores", errores);
        return handleExceptionInternal(ex, problema, headers, HttpStatus.BAD_REQUEST, request);
    }

    private static ProblemDetail problema(HttpStatus status, String title, String detail, String codigo, String ruta) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(status, detail);
        problema.setTitle(title);
        problema.setProperty("codigo", codigo);
        if (ruta != null) {
            problema.setInstance(URI.create(ruta));
        }
        return problema;
    }

    private static String ruta(WebRequest request) {
        if (request instanceof ServletWebRequest servlet) {
            return servlet.getRequest().getRequestURI();
        }
        return null;
    }

    private static String restriccion(Throwable ex) {
        Throwable actual = ex;
        while (actual != null) {
            if (actual.getMessage() != null) {
                Matcher matcher = CONSTRAINT.matcher(actual.getMessage());
                if (matcher.find()) {
                    return matcher.group(1);
                }
            }
            actual = actual.getCause();
        }
        return null;
    }

    private static String sqlState(Throwable ex) {
        Throwable actual = ex;
        while (actual != null) {
            if (actual instanceof SQLException sql && sql.getSQLState() != null) {
                return sql.getSQLState();
            }
            actual = actual.getCause();
        }
        return null;
    }
}
