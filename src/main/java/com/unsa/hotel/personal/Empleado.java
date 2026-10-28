package com.unsa.hotel.personal;

public abstract class Empleado {
    private final int id;
    private final String nombre;

    protected Empleado(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() { return nombre; }
}
