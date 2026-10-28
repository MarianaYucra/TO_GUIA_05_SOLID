package com.unsa.hotel.util;

import java.util.Locale;

/** Utilidad mínima para mostrar montos de forma consistente. */
public final class Formato {
    private Formato() { }

    public static String soles(double monto) {
        return String.format(Locale.US, "S/ %.2f", monto);
    }
}
