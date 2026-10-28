package com.unsa.hotel.personal;

import com.unsa.hotel.modelo.Reserva;

/** ISP (Ejercicio 6): rol específico de recepción. */
public interface IPersonalRecepcion {
    void registrarCheckIn(Reserva reserva);
    void registrarCheckOut(Reserva reserva);
}
