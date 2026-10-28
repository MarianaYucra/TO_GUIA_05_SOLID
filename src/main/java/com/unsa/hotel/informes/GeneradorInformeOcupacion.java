package com.unsa.hotel.informes;

import com.unsa.hotel.modelo.EstadoReserva;
import com.unsa.hotel.modelo.Habitacion;
import com.unsa.hotel.modelo.Reserva;
import com.unsa.hotel.modelo.TipoHabitacion;

import java.time.LocalDate;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/** SRP (Ejercicio 1.a.4): los informes salen de Habitacion y viven aquí. */
public class GeneradorInformeOcupacion {

    /** Porcentaje de ocupación por tipo en el periodo [desde, hasta). */
    public Map<TipoHabitacion, Double> ocupacionPorTipo(Collection<Habitacion> habitaciones,
                                                        Collection<Reserva> reservas,
                                                        LocalDate desde, LocalDate hasta) {
        long dias = hasta.toEpochDay() - desde.toEpochDay();
        Map<TipoHabitacion, Double> resultado = new EnumMap<>(TipoHabitacion.class);

        for (TipoHabitacion tipo : TipoHabitacion.values()) {
            long cantidad = habitaciones.stream().filter(h -> h.getTipo() == tipo).count();
            if (cantidad == 0) continue;
            long nochesOcupadas = reservas.stream()
                    .filter(r -> r.getEstado() != EstadoReserva.CANCELADA)
                    .filter(r -> r.getHabitacion().getTipo() == tipo)
                    .mapToLong(r -> nochesEnPeriodo(r, desde, hasta))
                    .sum();
            resultado.put(tipo, 100.0 * nochesOcupadas / (cantidad * dias));
        }
        return resultado;
    }

    private long nochesEnPeriodo(Reserva r, LocalDate desde, LocalDate hasta) {
        LocalDate ini = r.getFechaEntrada().isAfter(desde) ? r.getFechaEntrada() : desde;
        LocalDate fin = r.getFechaSalida().isBefore(hasta) ? r.getFechaSalida() : hasta;
        return Math.max(0, fin.toEpochDay() - ini.toEpochDay());
    }
}
