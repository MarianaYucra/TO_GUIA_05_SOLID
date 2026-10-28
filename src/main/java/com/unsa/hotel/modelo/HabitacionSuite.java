package com.unsa.hotel.modelo;

import com.unsa.hotel.disponibilidad.GestorDisponibilidadHabitacion;
import com.unsa.hotel.servicios.ServicioComida;
import com.unsa.hotel.servicios.ServicioLimpieza;

/** Suite: limpieza + servicio de comida a la habitación. */
public class HabitacionSuite extends Habitacion implements ServicioLimpieza, ServicioComida {

    public HabitacionSuite(int numero, double precioBase, GestorDisponibilidadHabitacion gestor) {
        super(numero, TipoHabitacion.SUITE, precioBase, gestor);
    }

    @Override public int getCapacidad() { return 4; }
    @Override protected double getRecargoCategoria() { return 80; }
    @Override public String getDescripcion() { return "Suite con sala de estar, jacuzzi y vista panorámica"; }

    @Override
    public String solicitarLimpieza() {
        return "Limpieza solicitada para la suite " + getNumero();
    }

    @Override
    public String solicitarComida(String plato) {
        return "Pedido '" + plato + "' enviado a la suite " + getNumero();
    }
}
