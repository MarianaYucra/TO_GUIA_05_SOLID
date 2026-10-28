package com.unsa.hotel.modelo;

import com.unsa.hotel.cancelacion.PoliticaCancelacion;
import com.unsa.hotel.cancelacion.ResultadoCancelacion;
import com.unsa.hotel.util.Formato;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * OCP (Ejercicio 2): Reserva ya no contiene reglas de cancelación; las delega en
 * la {@link PoliticaCancelacion} asociada. Agregar una política nueva no obliga
 * a modificar esta clase.
 */
public class Reserva {

    public static final LocalTime HORA_CHECK_IN = LocalTime.of(14, 0);
    private static final AtomicInteger SECUENCIA = new AtomicInteger(1);

    private final int id = SECUENCIA.getAndIncrement();
    private final Cliente cliente;
    private final Habitacion habitacion;
    private final LocalDate fechaEntrada;
    private final LocalDate fechaSalida;
    private final double precioTotal;
    private final PoliticaCancelacion politicaCancelacion;
    private EstadoReserva estado = EstadoReserva.CONFIRMADA;
    private double penalizacionCobrada;

    public Reserva(Cliente cliente, Habitacion habitacion, LocalDate fechaEntrada,
                   LocalDate fechaSalida, double precioTotal, PoliticaCancelacion politicaCancelacion) {
        if (!fechaSalida.isAfter(fechaEntrada)) {
            throw new IllegalArgumentException("La salida debe ser posterior a la entrada");
        }
        this.cliente = Objects.requireNonNull(cliente);
        this.habitacion = Objects.requireNonNull(habitacion);
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.precioTotal = precioTotal;
        this.politicaCancelacion = Objects.requireNonNull(politicaCancelacion);
    }

    /** Cancela usando la política asociada. */
    public ResultadoCancelacion cancelar(LocalDateTime momento) {
        if (estado != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException("Solo se pueden cancelar reservas confirmadas (estado: " + estado + ")");
        }
        if (!politicaCancelacion.puedeCancelar(this, momento)) {
            return ResultadoCancelacion.rechazada("La política " + politicaCancelacion.getNombre()
                    + " no permite cancelar en este momento");
        }
        double penalizacion = politicaCancelacion.calcularPenalizacion(this, momento);
        this.estado = EstadoReserva.CANCELADA;
        this.penalizacionCobrada = penalizacion;
        return ResultadoCancelacion.aceptada(penalizacion);
    }

    public void iniciarEstadia() {
        if (estado != EstadoReserva.CONFIRMADA) {
            throw new IllegalStateException("No se puede hacer check-in con estado " + estado);
        }
        estado = EstadoReserva.EN_CURSO;
    }

    public void finalizarEstadia() {
        if (estado != EstadoReserva.EN_CURSO) {
            throw new IllegalStateException("No se puede hacer check-out con estado " + estado);
        }
        estado = EstadoReserva.FINALIZADA;
    }

    public long getNoches() { return ChronoUnit.DAYS.between(fechaEntrada, fechaSalida); }

    public LocalDateTime getInstanteCheckIn() { return fechaEntrada.atTime(HORA_CHECK_IN); }

    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Habitacion getHabitacion() { return habitacion; }
    public LocalDate getFechaEntrada() { return fechaEntrada; }
    public LocalDate getFechaSalida() { return fechaSalida; }
    public double getPrecioTotal() { return precioTotal; }
    public PoliticaCancelacion getPoliticaCancelacion() { return politicaCancelacion; }
    public EstadoReserva getEstado() { return estado; }
    public double getPenalizacionCobrada() { return penalizacionCobrada; }

    @Override
    public String toString() {
        return "Reserva #" + id + " " + habitacion + " " + fechaEntrada + " -> " + fechaSalida
                + " (" + Formato.soles(precioTotal) + ", " + politicaCancelacion.getNombre() + ", " + estado + ")";
    }
}
