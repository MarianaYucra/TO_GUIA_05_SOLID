package com.unsa.hotel.modelo;

public enum EstadoReserva {
    CONFIRMADA, EN_CURSO, FINALIZADA, CANCELADA;

    /** Una reserva bloquea la habitación mientras esté confirmada o en curso. */
    public boolean bloqueaHabitacion() {
        return this == CONFIRMADA || this == EN_CURSO;
    }
}
