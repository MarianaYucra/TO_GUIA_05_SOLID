package com.unsa.hotel.notificacion;

/** Envío simulado a un canal de Slack (opcional según la guía). */
public class NotificadorSlack implements CanalNotificacion {
    private final String canal;

    public NotificadorSlack(String canal) { this.canal = canal; }

    @Override
    public void enviarNotificacion(String mensaje) {
        System.out.println("   [SLACK " + canal + "] " + mensaje);
    }
}
