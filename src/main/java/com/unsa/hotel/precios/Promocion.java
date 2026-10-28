package com.unsa.hotel.precios;

import com.unsa.hotel.modelo.Cliente;
import com.unsa.hotel.modelo.Habitacion;

import java.time.LocalDate;

/** OCP (Ejercicio 6): cada promoción es una estrategia intercambiable. */
public interface Promocion {

    String getNombre();

    boolean aplica(Habitacion habitacion, Cliente cliente, LocalDate entrada, LocalDate salida);

    /** Monto a descontar sobre el subtotal (>= 0). */
    double calcularDescuento(double subtotal);
}
