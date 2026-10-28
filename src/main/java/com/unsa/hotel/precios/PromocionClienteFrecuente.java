package com.unsa.hotel.precios;

import com.unsa.hotel.modelo.Cliente;
import com.unsa.hotel.modelo.Habitacion;

import java.time.LocalDate;

/** Descuento para clientes con al menos N reservas previas no canceladas. */
public class PromocionClienteFrecuente implements Promocion {

    private final int reservasMinimas;
    private final double porcentaje;

    public PromocionClienteFrecuente(int reservasMinimas, double porcentaje) {
        this.reservasMinimas = reservasMinimas;
        this.porcentaje = porcentaje;
    }

    @Override public String getNombre() { return "Cliente frecuente (" + (int) (porcentaje * 100) + "%)"; }

    @Override
    public boolean aplica(Habitacion habitacion, Cliente cliente, LocalDate entrada, LocalDate salida) {
        return cliente.cantidadReservasNoCanceladas() >= reservasMinimas;
    }

    @Override
    public double calcularDescuento(double subtotal) { return subtotal * porcentaje; }
}
