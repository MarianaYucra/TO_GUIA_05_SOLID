package com.unsa.hotel.cancelacion;

import com.unsa.hotel.util.Formato;

public record ResultadoCancelacion(boolean cancelada, double penalizacion, String mensaje) {

    public static ResultadoCancelacion aceptada(double penalizacion) {
        String msg = penalizacion == 0
                ? "Reserva cancelada sin penalización"
                : "Reserva cancelada con penalización de " + Formato.soles(penalizacion);
        return new ResultadoCancelacion(true, penalizacion, msg);
    }

    public static ResultadoCancelacion rechazada(String motivo) {
        return new ResultadoCancelacion(false, 0, motivo);
    }
}
