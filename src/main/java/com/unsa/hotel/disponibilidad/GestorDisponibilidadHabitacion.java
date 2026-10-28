package com.unsa.hotel.disponibilidad;

import com.unsa.hotel.modelo.Habitacion;
import com.unsa.hotel.modelo.Reserva;
import com.unsa.hotel.modelo.TipoHabitacion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * SRP (Ejercicio 1): única responsabilidad = saber si una habitación está libre
 * en un rango de fechas. Mantiene la lista de reservas necesaria para decidirlo.
 */
public class GestorDisponibilidadHabitacion {

    private final List<Reserva> reservas = new ArrayList<>();

    public void registrarReserva(Reserva reserva) {
        reservas.add(reserva);
    }

    /** Rango [entrada, salida): la noche de salida queda libre para otro huésped. */
    public boolean estaDisponible(Habitacion habitacion, LocalDate entrada, LocalDate salida) {
        if (!salida.isAfter(entrada)) {
            throw new IllegalArgumentException("La salida debe ser posterior a la entrada");
        }
        return reservas.stream()
                .filter(r -> r.getEstado().bloqueaHabitacion())
                .filter(r -> r.getHabitacion() == habitacion)
                .noneMatch(r -> entrada.isBefore(r.getFechaSalida()) && salida.isAfter(r.getFechaEntrada()));
    }

    public List<Habitacion> buscarDisponibles(Collection<Habitacion> habitaciones, LocalDate entrada,
                                              LocalDate salida, TipoHabitacion tipo) {
        return habitaciones.stream()
                .filter(h -> tipo == null || h.getTipo() == tipo)
                .filter(h -> estaDisponible(h, entrada, salida))
                .toList();
    }

    public List<Reserva> getReservas() {
        return Collections.unmodifiableList(reservas);
    }
}
