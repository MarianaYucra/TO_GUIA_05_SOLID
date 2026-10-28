package com.unsa.hotel.personal;

import com.unsa.hotel.modelo.Habitacion;

import java.util.List;

/** ISP (Ejercicio 6): rol específico de limpieza. */
public interface IPersonalLimpieza {
    void asignarHabitacion(Habitacion habitacion);
    List<Habitacion> getHabitacionesAsignadas();
    int getCargaTrabajo();
}
