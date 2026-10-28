package com.unsa.hotel.notificacion;

public class EnviadorCorreo implements CanalNotificacion {
    private final String remitente;

    public EnviadorCorreo(String remitente) { this.remitente = remitente; }

    @Override
    public void enviarNotificacion(String mensaje) {
        System.out.println("   [CORREO desde " + remitente + "] " + mensaje);
    }
}
