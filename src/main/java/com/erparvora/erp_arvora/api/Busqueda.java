package com.erparvora.erp_arvora.api;

import java.util.Locale;

public final class Busqueda {

    private Busqueda() {
    }

    public static boolean presente(String texto) {
        return texto != null && !texto.isBlank();
    }

    /** Patrón LIKE en minúsculas, con % y _ del usuario escapados. */
    public static String patron(String texto) {
        String limpio = texto.trim().toLowerCase(Locale.ROOT)
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        return "%" + limpio + "%";
    }
}
