package com.unsa.hotel.notificacion;

/** DIP (Ejercicio 5): abstracción de la que dependen los módulos de alto nivel. */
public interface CanalNotificacion {
    void enviarNotificacion(String mensaje);
}
