package com.unsa.hotel.modelo;

import com.unsa.hotel.disponibilidad.GestorDisponibilidadHabitacion;
import com.unsa.hotel.servicios.ServicioLimpieza;

/** ISP: la habitación estándar solo ofrece servicio de limpieza. */
public class HabitacionEstandar extends Habitacion implements ServicioLimpieza {

    public HabitacionEstandar(int numero, double precioBase, GestorDisponibilidadHabitacion gestor) {
        super(numero, TipoHabitacion.ESTANDAR, precioBase, gestor);
    }

    @Override public int getCapacidad() { return 2; }
    @Override protected double getRecargoCategoria() { return 0; }
    @Override public String getDescripcion() { return "Habitación estándar con cama doble y baño privado"; }

    @Override
    public String solicitarLimpieza() {
        return "Limpieza solicitada para la habitación " + getNumero();
    }
}
