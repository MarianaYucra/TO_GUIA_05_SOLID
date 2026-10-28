package com.unsa.hotel.modelo;

import com.unsa.hotel.disponibilidad.GestorDisponibilidadHabitacion;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Clase base de todas las habitaciones.
 *
 * SRP (Ejercicio 1): Habitacion solo conoce sus propios datos y su estado físico
 * (ocupada / libre). La disponibilidad por fechas se delega en
 * {@link GestorDisponibilidadHabitacion}; el precio en CalculadoraPrecioHabitacion;
 * los informes en las clases del paquete "informes".
 *
 * LSP (Ejercicio 3) - contrato que TODA subclase debe respetar:
 *  - getCapacidad() devuelve un entero >= 1.
 *  - getRecargoCategoria() devuelve un valor >= 0 (nunca reduce el precio base).
 *  - getPrecioPorNoche() es final: el cálculo no puede alterarse en subclases.
 *  - Ninguna subclase lanza excepciones nuevas en los métodos heredados.
 */
public abstract class Habitacion {

    private final int numero;
    private final TipoHabitacion tipo;
    private final double precioBase;
    private final GestorDisponibilidadHabitacion gestorDisponibilidad;
    private boolean ocupada;

    protected Habitacion(int numero, TipoHabitacion tipo, double precioBase,
                         GestorDisponibilidadHabitacion gestorDisponibilidad) {
        if (precioBase <= 0) {
            throw new IllegalArgumentException("El precio base debe ser mayor que cero");
        }
        this.numero = numero;
        this.tipo = Objects.requireNonNull(tipo);
        this.precioBase = precioBase;
        this.gestorDisponibilidad = Objects.requireNonNull(gestorDisponibilidad);
    }

    // ---- Disponibilidad: se delega (Ejercicio 1.c) ----
    public boolean estaDisponible(LocalDate entrada, LocalDate salida) {
        return gestorDisponibilidad.estaDisponible(this, entrada, salida);
    }

    // ---- Estado físico (check-in / check-out) ----
    public void ocupar() {
        if (ocupada) {
            throw new IllegalStateException("La habitación " + numero + " ya está ocupada");
        }
        ocupada = true;
    }

    public void liberar() {
        ocupada = false;
    }

    public boolean isOcupada() { return ocupada; }

    // ---- Contrato para subclases ----
    public abstract int getCapacidad();

    /** Recargo por categoría, siempre >= 0. */
    protected abstract double getRecargoCategoria();

    public abstract String getDescripcion();

    public final double getPrecioPorNoche() {
        double recargo = getRecargoCategoria();
        if (recargo < 0) {
            throw new IllegalStateException("El recargo de categoría no puede ser negativo");
        }
        return precioBase + recargo;
    }

    public int getNumero() { return numero; }
    public TipoHabitacion getTipo() { return tipo; }
    public double getPrecioBase() { return precioBase; }

    @Override
    public String toString() {
        return "Hab. " + numero + " [" + tipo + "]";
    }
}
