package com.unsa.hotel.precios;

import com.unsa.hotel.modelo.Cliente;
import com.unsa.hotel.modelo.Habitacion;

import java.time.LocalDate;

/** Campaña con descuento para estadías que se cruzan con un rango de fechas (p. ej. fiestas). */
public class PromocionTemporadaAlta implements Promocion {

    private final LocalDate desde;
    private final LocalDate hasta;
    private final double porcentaje;

    public PromocionTemporadaAlta(LocalDate desde, LocalDate hasta, double porcentaje) {
        this.desde = desde;
        this.hasta = hasta;
        this.porcentaje = porcentaje;
    }

    @Override public String getNombre() { return "Temporada alta (" + (int) (porcentaje * 100) + "%)"; }

    @Override
    public boolean aplica(Habitacion habitacion, Cliente cliente, LocalDate entrada, LocalDate salida) {
        return !entrada.isAfter(hasta) && salida.isAfter(desde);
    }

    @Override
    public double calcularDescuento(double subtotal) { return subtotal * porcentaje; }
}
