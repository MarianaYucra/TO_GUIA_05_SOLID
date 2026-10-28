package com.unsa.hotel.cancelacion;

import com.unsa.hotel.modelo.Reserva;

import java.time.LocalDateTime;

/** No permite cancelaciones una vez realizada la reserva. */
public class PoliticaCancelacionEstricta implements PoliticaCancelacion {

    @Override public String getNombre() { return "Estricta"; }

    @Override
    public boolean puedeCancelar(Reserva reserva, LocalDateTime momento) {
        return false;
    }

    @Override
    public double calcularPenalizacion(Reserva reserva, LocalDateTime momento) {
        return reserva.getPrecioTotal();
    }
}
