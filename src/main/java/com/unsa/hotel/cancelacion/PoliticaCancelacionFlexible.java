package com.unsa.hotel.cancelacion;

import com.unsa.hotel.modelo.Reserva;

import java.time.Duration;
import java.time.LocalDateTime;

/** Cancelación gratuita hasta 24 horas antes del check-in. */
public class PoliticaCancelacionFlexible implements PoliticaCancelacion {

    private static final long HORAS_MINIMAS = 24;

    @Override public String getNombre() { return "Flexible"; }

    @Override
    public boolean puedeCancelar(Reserva reserva, LocalDateTime momento) {
        return Duration.between(momento, reserva.getInstanteCheckIn()).toHours() >= HORAS_MINIMAS;
    }

    @Override
    public double calcularPenalizacion(Reserva reserva, LocalDateTime momento) {
        return 0;
    }
}
