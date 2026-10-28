package com.unsa.hotel.cancelacion;

import com.unsa.hotel.modelo.Reserva;

import java.time.LocalDateTime;

/** OCP (Ejercicio 2): abstracción que permite añadir políticas sin tocar Reserva. */
public interface PoliticaCancelacion {

    String getNombre();

    boolean puedeCancelar(Reserva reserva, LocalDateTime momento);

    double calcularPenalizacion(Reserva reserva, LocalDateTime momento);

    default boolean puedeCancelar(Reserva reserva) {
        return puedeCancelar(reserva, LocalDateTime.now());
    }
}
