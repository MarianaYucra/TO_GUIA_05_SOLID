package com.unsa.hotel.notificacion;

/** Envío simulado de SMS. */
public class EnviadorSMS implements CanalNotificacion {
    @Override
    public void enviarNotificacion(String mensaje) {
        System.out.println("   [SMS] " + mensaje);
    }
}
