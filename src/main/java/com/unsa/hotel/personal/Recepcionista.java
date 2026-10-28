package com.unsa.hotel.personal;

import com.unsa.hotel.modelo.Reserva;

public class Recepcionista extends Empleado implements IPersonalRecepcion {

    public Recepcionista(int id, String nombre) { super(id, nombre); }

    @Override
    public void registrarCheckIn(Reserva reserva) {
        reserva.iniciarEstadia();
        reserva.getHabitacion().ocupar();
    }

    @Override
    public void registrarCheckOut(Reserva reserva) {
        reserva.finalizarEstadia();
        reserva.getHabitacion().liberar();
    }
}
