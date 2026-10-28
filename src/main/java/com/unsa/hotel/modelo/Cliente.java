package com.unsa.hotel.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cliente {
    private final int id;
    private final String nombre;
    private final String email;
    private final String telefono;
    private final List<Reserva> historial = new ArrayList<>();

    public Cliente(int id, String nombre, String email, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }

    public void agregarReserva(Reserva reserva) { historial.add(reserva); }

    public List<Reserva> getHistorial() { return Collections.unmodifiableList(historial); }

    /** Reservas que no fueron canceladas (usado por promociones de cliente frecuente). */
    public long cantidadReservasNoCanceladas() {
        return historial.stream().filter(r -> r.getEstado() != EstadoReserva.CANCELADA).count();
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }

    @Override
    public String toString() { return nombre + " <" + email + ">"; }
}
