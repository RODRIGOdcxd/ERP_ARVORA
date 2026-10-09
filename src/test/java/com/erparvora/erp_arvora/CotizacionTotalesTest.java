package com.erparvora.erp_arvora;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.erparvora.erp_arvora.model.Cotizacion;
import com.erparvora.erp_arvora.model.CotizacionLinea;

class CotizacionTotalesTest {

    @Test
    void preciosConIgvDejanElSubtotalSinImpuesto() {
        CotizacionLinea linea = linea("2.0000", "59.00");
        Cotizacion cotizacion = new Cotizacion();

        cotizacion.recalcularTotalesDesdeLineas(List.of(linea));

        assertThat(linea.getImporte()).isEqualByComparingTo("118.00");
        assertThat(cotizacion.isPreciosIncluyenIgv()).isTrue();
        assertThat(cotizacion.getSubtotal()).isEqualByComparingTo("100.00");
        assertThat(cotizacion.getIgv()).isEqualByComparingTo("18.00");
        assertThat(cotizacion.getTotal()).isEqualByComparingTo("118.00");
    }

    @Test
    void preciosSinIgvSumanElImpuestoAlTotal() {
        CotizacionLinea linea = linea("1.0000", "100.00");
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setPreciosIncluyenIgv(false);

        cotizacion.recalcularTotalesDesdeLineas(List.of(linea));

        assertThat(cotizacion.getSubtotal()).isEqualByComparingTo("100.00");
        assertThat(cotizacion.getIgv()).isEqualByComparingTo("18.00");
        assertThat(cotizacion.getTotal()).isEqualByComparingTo("118.00");
    }

    @Test
    void elDescuentoDeCabeceraNoPuedeDejarElNetoNegativo() {
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setDescuento(new BigDecimal("20.00"));

        assertThatThrownBy(() -> cotizacion.recalcularTotales(new BigDecimal("10.00")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static CotizacionLinea linea(String cantidad, String precio) {
        CotizacionLinea linea = new CotizacionLinea();
        linea.setCantidad(new BigDecimal(cantidad));
        linea.setPrecioUnitario(new BigDecimal(precio));
        linea.recalcularImporte();
        return linea;
    }
}
