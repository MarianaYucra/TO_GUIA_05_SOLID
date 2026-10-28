package com.unsa.hotel.cancelacion;

import com.unsa.hotel.modelo.Reserva;

import java.time.Duration;
import java.time.LocalDateTime;

/** Cancelación hasta 72 horas antes del check-in con penalización del 50 %. */
public class PoliticaCancelacionModerada implements PoliticaCancelacion {

    private static final long HORAS_MINIMAS = 72;
    private static final double PORCENTAJE_PENALIZACION = 0.50;

    @Override public String getNombre() { return "Moderada"; }

    @Override
    public boolean puedeCancelar(Reserva reserva, LocalDateTime momento) {
        return Duration.between(momento, reserva.getInstanteCheckIn()).toHours() >= HORAS_MINIMAS;
    }

    @Override
    public double calcularPenalizacion(Reserva reserva, LocalDateTime momento) {
        return reserva.getPrecioTotal() * PORCENTAJE_PENALIZACION;
    }
}
