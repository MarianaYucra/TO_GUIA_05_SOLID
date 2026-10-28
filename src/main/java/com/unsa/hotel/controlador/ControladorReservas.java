package com.unsa.hotel.controlador;

import com.unsa.hotel.cancelacion.PoliticaCancelacion;
import com.unsa.hotel.cancelacion.PoliticaCancelacionEstricta;
import com.unsa.hotel.cancelacion.PoliticaCancelacionFlexible;
import com.unsa.hotel.cancelacion.PoliticaCancelacionModerada;
import com.unsa.hotel.cancelacion.ResultadoCancelacion;
import com.unsa.hotel.disponibilidad.GestorDisponibilidadHabitacion;
import com.unsa.hotel.modelo.Cliente;
import com.unsa.hotel.modelo.Habitacion;
import com.unsa.hotel.modelo.Reserva;
import com.unsa.hotel.modelo.TipoHabitacion;
import com.unsa.hotel.notificacion.NotificadorReserva;
import com.unsa.hotel.personal.IPersonalLimpieza;
import com.unsa.hotel.personal.IPersonalRecepcion;
import com.unsa.hotel.precios.CalculadoraPrecioHabitacion;
import com.unsa.hotel.servicios.ServicioComida;
import com.unsa.hotel.servicios.ServicioLavanderia;
import com.unsa.hotel.servicios.ServicioLimpieza;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Orquesta los casos de uso. Solo depende de abstracciones (DIP) que recibe por constructor.
 * Trabaja con Habitacion sin conocer subclases (LSP) y descubre servicios con instanceof (ISP).
 */
public class ControladorReservas {

    private final GestorDisponibilidadHabitacion gestorDisponibilidad;
    private final CalculadoraPrecioHabitacion calculadoraPrecio;
    private final NotificadorReserva notificador;
    private final IPersonalRecepcion recepcion;

    private final List<Habitacion> habitaciones = new ArrayList<>();
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<IPersonalLimpieza> personalLimpieza = new ArrayList<>();
    private final List<Reserva> reservas = new ArrayList<>();
    private final Map<String, PoliticaCancelacion> politicas = new LinkedHashMap<>();

    public ControladorReservas(GestorDisponibilidadHabitacion gestorDisponibilidad,
                               CalculadoraPrecioHabitacion calculadoraPrecio,
                               NotificadorReserva notificador,
                               IPersonalRecepcion recepcion) {
        this.gestorDisponibilidad = gestorDisponibilidad;
        this.calculadoraPrecio = calculadoraPrecio;
        this.notificador = notificador;
        this.recepcion = recepcion;
        // Catálogo inicial. Una política nueva se añade con registrarPolitica() sin modificar este código.
        registrarPolitica(new PoliticaCancelacionFlexible());
        registrarPolitica(new PoliticaCancelacionModerada());
        registrarPolitica(new PoliticaCancelacionEstricta());
    }

    // ---------- Registro ----------
    public void registrarHabitacion(Habitacion h) { habitaciones.add(h); }
    public void registrarCliente(Cliente c) { clientes.add(c); }
    public void registrarPersonalLimpieza(IPersonalLimpieza p) { personalLimpieza.add(p); }

    public void registrarPolitica(PoliticaCancelacion politica) {
        politicas.put(politica.getNombre().toLowerCase(), politica);
    }

    public Collection<PoliticaCancelacion> listarPoliticas() { return politicas.values(); }

    /** Permite al usuario elegir la política por nombre al crear la reserva. */
    public PoliticaCancelacion seleccionarPolitica(String nombre) {
        PoliticaCancelacion p = politicas.get(nombre.toLowerCase());
        if (p == null) throw new IllegalArgumentException("Política desconocida: " + nombre);
        return p;
    }

    // ---------- Disponibilidad (usa el gestor, Ejercicio 1.d) ----------
    public List<Habitacion> consultarDisponibles(LocalDate entrada, LocalDate salida, TipoHabitacion tipo) {
        return gestorDisponibilidad.buscarDisponibles(habitaciones, entrada, salida, tipo);
    }

    // ---------- Reservas ----------
    public Reserva crearReserva(Cliente cliente, Habitacion habitacion, LocalDate entrada,
                                LocalDate salida, PoliticaCancelacion politica) {
        if (!habitacion.estaDisponible(entrada, salida)) {
            throw new IllegalStateException(habitacion + " no está disponible del " + entrada + " al " + salida);
        }
        // El precio se calcula ANTES de agregar la reserva al historial (cliente frecuente).
        double precio = calculadoraPrecio.calcularPrecioFinal(habitacion, cliente, entrada, salida);
        Reserva reserva = new Reserva(cliente, habitacion, entrada, salida, precio, politica);
        gestorDisponibilidad.registrarReserva(reserva);
        reservas.add(reserva);
        cliente.agregarReserva(reserva);
        notificador.notificarReservaCreada(reserva);
        return reserva;
    }

    public ResultadoCancelacion cancelarReserva(Reserva reserva, LocalDateTime momento) {
        ResultadoCancelacion resultado = reserva.cancelar(momento);
        notificador.notificarCancelacion(reserva, resultado);
        return resultado;
    }

    // ---------- Check-in / check-out ----------
    public void checkIn(Reserva reserva) { recepcion.registrarCheckIn(reserva); }

    /** Libera la habitación y asigna limpieza al empleado con menos carga. */
    public IPersonalLimpieza checkOut(Reserva reserva) {
        recepcion.registrarCheckOut(reserva);
        IPersonalLimpieza elegido = personalLimpieza.stream()
                .min(Comparator.comparingInt(IPersonalLimpieza::getCargaTrabajo))
                .orElseThrow(() -> new IllegalStateException("No hay personal de limpieza registrado"));
        elegido.asignarHabitacion(reserva.getHabitacion());
        return elegido;
    }

    // ---------- Consultas ----------
    public List<Reserva> consultarHistorial(Cliente cliente) { return cliente.getHistorial(); }

    public Map<IPersonalLimpieza, Integer> consultarCargaTrabajo() {
        Map<IPersonalLimpieza, Integer> mapa = new LinkedHashMap<>();
        personalLimpieza.forEach(p -> mapa.put(p, p.getCargaTrabajo()));
        return mapa;
    }

    // ---------- Servicios (ISP, Ejercicio 4.d) ----------
    public List<String> listarServicios(Habitacion h) {
        List<String> servicios = new ArrayList<>();
        if (h instanceof ServicioLimpieza) servicios.add("Limpieza");
        if (h instanceof ServicioComida) servicios.add("Comida a la habitación");
        if (h instanceof ServicioLavanderia) servicios.add("Lavandería");
        return servicios;
    }

    public String solicitarComida(Habitacion h, String plato) {
        return (h instanceof ServicioComida sc) ? sc.solicitarComida(plato)
                : h + " no ofrece servicio de comida";
    }

    public String solicitarLavanderia(Habitacion h, int prendas) {
        return (h instanceof ServicioLavanderia sl) ? sl.solicitarLavanderia(prendas)
                : h + " no ofrece servicio de lavandería";
    }

    public String solicitarLimpieza(Habitacion h) {
        return (h instanceof ServicioLimpieza sl) ? sl.solicitarLimpieza()
                : h + " no ofrece servicio de limpieza";
    }

    // ---------- Acceso de solo lectura (para informes) ----------
    public List<Habitacion> getHabitaciones() { return Collections.unmodifiableList(habitaciones); }
    public List<Reserva> getReservas() { return Collections.unmodifiableList(reservas); }
    public GestorDisponibilidadHabitacion getGestorDisponibilidad() { return gestorDisponibilidad; }
}
