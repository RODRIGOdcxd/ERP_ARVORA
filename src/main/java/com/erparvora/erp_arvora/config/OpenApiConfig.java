package com.erparvora.erp_arvora.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI erpOpenApi() {
        return new OpenAPI().info(new Info()
                .title("ERP ARVORA")
                .version("v1")
                .description("""
                        API única para el panel de administración (web, móvil y escritorio).
                        Los precios de lista incluyen IGV (18%). Las cotizaciones guardan si el precio incluye IGV \
                        y calculan el subtotal sin impuesto.
                        Los movimientos de inventario son un libro: solo se crean y se consultan. \
                        Una corrección es un movimiento nuevo de signo contrario (AJUSTE o DEVOLUCION).
                        Estos endpoints están abiertos. El siguiente paso es Spring Security con JWT y los roles \
                        ADMIN, VENTAS y TALLER; hasta entonces no hay control de acceso.
                        """));
    }
}
