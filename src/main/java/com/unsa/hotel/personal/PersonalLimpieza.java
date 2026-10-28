package com.unsa.hotel.personal;

import com.unsa.hotel.modelo.Habitacion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PersonalLimpieza extends Empleado implements IPersonalLimpieza {

    private final List<Habitacion> asignadas = new ArrayList<>();

    public PersonalLimpieza(int id, String nombre) { super(id, nombre); }

    @Override
    public void asignarHabitacion(Habitacion habitacion) { asignadas.add(habitacion); }

    @Override
    public List<Habitacion> getHabitacionesAsignadas() { return Collections.unmodifiableList(asignadas); }

    @Override
    public int getCargaTrabajo() { return asignadas.size(); }
}
