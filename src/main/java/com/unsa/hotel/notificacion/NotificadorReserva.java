package com.unsa.hotel.notificacion;

import com.unsa.hotel.cancelacion.ResultadoCancelacion;
import com.unsa.hotel.modelo.Reserva;
import com.unsa.hotel.util.Formato;

import java.util.Objects;

/** DIP: depende de CanalNotificacion (abstracción), recibida por constructor. */
public class NotificadorReserva {

    private final CanalNotificacion canal;

    public NotificadorReserva(CanalNotificacion canal) {
        this.canal = Objects.requireNonNull(canal);
    }

    public void notificarReservaCreada(Reserva r) {
        canal.enviarNotificacion("Hola " + r.getCliente().getNombre() + ", tu reserva #" + r.getId()
                + " en la " + r.getHabitacion() + " del " + r.getFechaEntrada() + " al " + r.getFechaSalida()
                + " fue confirmada. Total: " + Formato.soles(r.getPrecioTotal()));
    }

    public void notificarCancelacion(Reserva r, ResultadoCancelacion resultado) {
        canal.enviarNotificacion("Reserva #" + r.getId() + " de " + r.getCliente().getNombre() + ": "
                + resultado.mensaje());
    }
}
