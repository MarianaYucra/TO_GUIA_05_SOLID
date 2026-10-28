package com.unsa.hotel.modelo;

import com.unsa.hotel.disponibilidad.GestorDisponibilidadHabitacion;
import com.unsa.hotel.servicios.ServicioComida;
import com.unsa.hotel.servicios.ServicioLavanderia;
import com.unsa.hotel.servicios.ServicioLimpieza;

/** Premium: limpieza + comida + lavandería. */
public class HabitacionPremium extends Habitacion
        implements ServicioLimpieza, ServicioComida, ServicioLavanderia {

    public HabitacionPremium(int numero, double precioBase, GestorDisponibilidadHabitacion gestor) {
        super(numero, TipoHabitacion.PREMIUM, precioBase, gestor);
    }

    @Override public int getCapacidad() { return 6; }
    @Override protected double getRecargoCategoria() { return 200; }
    @Override public String getDescripcion() { return "Premium con dos dormitorios, terraza privada y mayordomo"; }

    @Override
    public String solicitarLimpieza() {
        return "Limpieza prioritaria solicitada para la habitación premium " + getNumero();
    }

    @Override
    public String solicitarComida(String plato) {
        return "Pedido '" + plato + "' enviado a la habitación premium " + getNumero();
    }

    @Override
    public String solicitarLavanderia(int prendas) {
        return "Recojo de " + prendas + " prendas en la habitación premium " + getNumero();
    }
}
