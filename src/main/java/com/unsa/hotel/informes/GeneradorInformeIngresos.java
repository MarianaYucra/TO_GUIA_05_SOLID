package com.unsa.hotel.informes;

import com.unsa.hotel.modelo.EstadoReserva;
import com.unsa.hotel.modelo.Reserva;

import java.time.LocalDate;
import java.util.Collection;

/** Ingresos = reservas no canceladas + penalizaciones cobradas, según fecha de entrada. */
public class GeneradorInformeIngresos {

    public double ingresosPorPeriodo(Collection<Reserva> reservas, LocalDate desde, LocalDate hasta) {
        return reservas.stream()
                .filter(r -> !r.getFechaEntrada().isBefore(desde) && r.getFechaEntrada().isBefore(hasta))
                .mapToDouble(r -> r.getEstado() == EstadoReserva.CANCELADA
                        ? r.getPenalizacionCobrada()
                        : r.getPrecioTotal())
                .sum();
    }
}
