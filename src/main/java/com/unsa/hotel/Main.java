package com.unsa.hotel;

import com.unsa.hotel.cancelacion.PoliticaCancelacion;
import com.unsa.hotel.cancelacion.PoliticaCancelacionEstricta;
import com.unsa.hotel.cancelacion.ResultadoCancelacion;
import com.unsa.hotel.controlador.ControladorReservas;
import com.unsa.hotel.disponibilidad.GestorDisponibilidadHabitacion;
import com.unsa.hotel.informes.GeneradorInformeIngresos;
import com.unsa.hotel.informes.GeneradorInformeOcupacion;
import com.unsa.hotel.modelo.Cliente;
import com.unsa.hotel.modelo.Habitacion;
import com.unsa.hotel.modelo.HabitacionEstandar;
import com.unsa.hotel.modelo.HabitacionPremium;
import com.unsa.hotel.modelo.HabitacionSuite;
import com.unsa.hotel.modelo.Reserva;
import com.unsa.hotel.modelo.TipoHabitacion;
import com.unsa.hotel.notificacion.CanalNotificacion;
import com.unsa.hotel.notificacion.EnviadorCorreo;
import com.unsa.hotel.notificacion.EnviadorSMS;
import com.unsa.hotel.notificacion.NotificadorReserva;
import com.unsa.hotel.notificacion.NotificadorSlack;
import com.unsa.hotel.personal.PersonalLimpieza;
import com.unsa.hotel.personal.Recepcionista;
import com.unsa.hotel.precios.CalculadoraPrecioHabitacion;
import com.unsa.hotel.precios.Promocion;
import com.unsa.hotel.precios.PromocionClienteFrecuente;
import com.unsa.hotel.precios.PromocionTemporadaAlta;
import com.unsa.hotel.util.Formato;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Demostración de los 6 ejercicios. Ejecutar sin argumentos corre todos;
 * con un número (1-6) corre solo ese ejercicio. Útil para las capturas del informe.
 */
public class Main {

    private static final LocalDate ENTRADA = LocalDate.of(2026, 12, 20);
    private static final LocalDate SALIDA = LocalDate.of(2026, 12, 24);

    public static void main(String[] args) {
        String opcion = args.length > 0 ? args[0] : "todos";
        if (opcion.equals("todos") || opcion.equals("1")) ejercicio1();
        if (opcion.equals("todos") || opcion.equals("2")) ejercicio2();
        if (opcion.equals("todos") || opcion.equals("3")) ejercicio3();
        if (opcion.equals("todos") || opcion.equals("4")) ejercicio4();
        if (opcion.equals("todos") || opcion.equals("5")) ejercicio5();
        if (opcion.equals("todos") || opcion.equals("6")) ejercicio6();
    }

    // ------------------------------------------------------------------
    private static Escenario nuevoEscenario(CanalNotificacion canal, List<Promocion> promociones) {
        GestorDisponibilidadHabitacion gestor = new GestorDisponibilidadHabitacion();
        ControladorReservas ctrl = new ControladorReservas(
                gestor,
                new CalculadoraPrecioHabitacion(promociones),
                new NotificadorReserva(canal),          // DIP: canal inyectado
                new Recepcionista(1, "Lucía Mendoza"));
        Habitacion h101 = new HabitacionEstandar(101, 150, gestor);
        Habitacion h201 = new HabitacionSuite(201, 300, gestor);
        Habitacion h301 = new HabitacionPremium(301, 500, gestor);
        List.of(h101, h201, h301).forEach(ctrl::registrarHabitacion);
        ctrl.registrarPersonalLimpieza(new PersonalLimpieza(1, "Rosa Quispe"));
        ctrl.registrarPersonalLimpieza(new PersonalLimpieza(2, "Mario Condori"));
        Cliente ana = new Cliente(1, "Ana Torres", "ana@mail.com", "999111222");
        Cliente luis = new Cliente(2, "Luis Paredes", "luis@mail.com", "999333444");
        ctrl.registrarCliente(ana);
        ctrl.registrarCliente(luis);
        return new Escenario(ctrl, h101, h201, h301, ana, luis);
    }

    private record Escenario(ControladorReservas ctrl, Habitacion h101, Habitacion h201,
                             Habitacion h301, Cliente ana, Cliente luis) { }

    private static void titulo(String t) {
        System.out.println("\n==================================================");
        System.out.println(t);
        System.out.println("==================================================");
    }

    // ------------------------------------------------------------------
    private static void ejercicio1() {
        titulo("EJERCICIO 1 - SRP: GestorDisponibilidadHabitacion");
        Escenario e = nuevoEscenario(new EnviadorCorreo("reservas@hotel.pe"), List.of());
        System.out.println("Disponibles antes de reservar (" + ENTRADA + " a " + SALIDA + "): "
                + e.ctrl().consultarDisponibles(ENTRADA, SALIDA, null));
        e.ctrl().crearReserva(e.ana(), e.h201(), ENTRADA, SALIDA, e.ctrl().seleccionarPolitica("flexible"));
        System.out.println("Suite 201 disponible mismas fechas?  " + e.h201().estaDisponible(ENTRADA, SALIDA));
        System.out.println("Suite 201 disponible del 24 al 26?   "
                + e.h201().estaDisponible(SALIDA, SALIDA.plusDays(2)));
        System.out.println("Disponibles ahora: " + e.ctrl().consultarDisponibles(ENTRADA, SALIDA, null));
        try {
            e.ctrl().crearReserva(e.luis(), e.h201(), ENTRADA.plusDays(1), SALIDA, e.ctrl().seleccionarPolitica("flexible"));
        } catch (IllegalStateException ex) {
            System.out.println("Reserva solapada rechazada: " + ex.getMessage());
        }
    }

    // ------------------------------------------------------------------
    private static void ejercicio2() {
        titulo("EJERCICIO 2 - OCP: Políticas de cancelación");
        Escenario e = nuevoEscenario(new EnviadorCorreo("reservas@hotel.pe"), List.of());
        System.out.println("Políticas disponibles: "
                + e.ctrl().listarPoliticas().stream().map(PoliticaCancelacion::getNombre).toList());

        Reserva flex = e.ctrl().crearReserva(e.ana(), e.h101(), ENTRADA, SALIDA, e.ctrl().seleccionarPolitica("Flexible"));
        Reserva mod = e.ctrl().crearReserva(e.ana(), e.h201(), ENTRADA, SALIDA, e.ctrl().seleccionarPolitica("Moderada"));
        Reserva est = e.ctrl().crearReserva(e.luis(), e.h301(), ENTRADA, SALIDA, e.ctrl().seleccionarPolitica("Estricta"));

        LocalDateTime checkIn = ENTRADA.atTime(14, 0);
        System.out.println("\n-- Flexible, cancelando 30 h antes:");
        mostrar(e.ctrl().cancelarReserva(flex, checkIn.minusHours(30)));
        System.out.println("-- Moderada, cancelando 48 h antes (muy tarde):");
        mostrar(e.ctrl().cancelarReserva(mod, checkIn.minusHours(48)));
        System.out.println("-- Moderada, cancelando 100 h antes:");
        mostrar(e.ctrl().cancelarReserva(mod, checkIn.minusHours(100)));
        System.out.println("-- Estricta, cancelando 500 h antes:");
        mostrar(e.ctrl().cancelarReserva(est, checkIn.minusHours(500)));

        // Extensión sin modificar Reserva ni el controlador: nueva política creada "al vuelo".
        PoliticaCancelacion sinReembolsoPrimerDia = new PoliticaCancelacionEstricta() {
            @Override public String getNombre() { return "Personalizada"; }
            @Override public boolean puedeCancelar(Reserva r, LocalDateTime m) { return true; }
            @Override public double calcularPenalizacion(Reserva r, LocalDateTime m) { return 0; }
        };
        e.ctrl().registrarPolitica(sinReembolsoPrimerDia);
        System.out.println("\nNueva política registrada sin tocar código existente: "
                + e.ctrl().seleccionarPolitica("personalizada").getNombre());
    }

    private static void mostrar(ResultadoCancelacion r) {
        System.out.println("   => cancelada=" + r.cancelada() + " | penalización=" + Formato.soles(r.penalizacion()));
    }

    // ------------------------------------------------------------------
    private static void ejercicio3() {
        titulo("EJERCICIO 3 - LSP: Subclases de Habitacion intercambiables");
        Escenario e = nuevoEscenario(new EnviadorCorreo("reservas@hotel.pe"), List.of());
        for (Habitacion h : e.ctrl().getHabitaciones()) {   // el controlador solo ve "Habitacion"
            System.out.printf("%-22s capacidad=%d | precio/noche=%s | %s%n",
                    h, h.getCapacidad(), Formato.soles(h.getPrecioPorNoche()), h.getDescripcion());
        }
        for (Habitacion h : e.ctrl().getHabitaciones()) {
            h.ocupar();
            h.liberar();                                       // mismo contrato en todas
        }
        System.out.println("Check-in/check-out ejecutado sobre todas las subclases sin errores.");
    }

    // ------------------------------------------------------------------
    private static void ejercicio4() {
        titulo("EJERCICIO 4 - ISP: Servicios segregados");
        Escenario e = nuevoEscenario(new EnviadorCorreo("reservas@hotel.pe"), List.of());
        for (Habitacion h : e.ctrl().getHabitaciones()) {
            System.out.println(h + " -> servicios: " + e.ctrl().listarServicios(h));
        }
        System.out.println(e.ctrl().solicitarLimpieza(e.h101()));
        System.out.println(e.ctrl().solicitarComida(e.h101(), "Lomo saltado"));
        System.out.println(e.ctrl().solicitarComida(e.h201(), "Lomo saltado"));
        System.out.println(e.ctrl().solicitarLavanderia(e.h201(), 5));
        System.out.println(e.ctrl().solicitarLavanderia(e.h301(), 5));
    }

    // ------------------------------------------------------------------
    private static void ejercicio5() {
        titulo("EJERCICIO 5 - DIP: Canales de notificación");
        CanalNotificacion[] canales = {
                new EnviadorCorreo("reservas@hotel.pe"), new EnviadorSMS(), new NotificadorSlack("#reservas")
        };
        for (CanalNotificacion canal : canales) {
            System.out.println("Canal: " + canal.getClass().getSimpleName());
            Escenario e = nuevoEscenario(canal, List.of());
            Reserva r = e.ctrl().crearReserva(e.ana(), e.h101(), ENTRADA, SALIDA, e.ctrl().seleccionarPolitica("flexible"));
            e.ctrl().cancelarReserva(r, ENTRADA.atTime(14, 0).minusHours(48));
        }
    }

    // ------------------------------------------------------------------
    private static void ejercicio6() {
        titulo("EJERCICIO 6 - SOLID integrado");
        List<Promocion> promos = List.of(
                new PromocionTemporadaAlta(LocalDate.of(2026, 12, 15), LocalDate.of(2027, 1, 5), 0.10),
                new PromocionClienteFrecuente(3, 0.15));
        Escenario e = nuevoEscenario(new EnviadorSMS(), promos);
        ControladorReservas c = e.ctrl();

        System.out.println("-- Ana construye historial (3 estadías previas)");
        for (int i = 0; i < 3; i++) {
            LocalDate in = LocalDate.of(2026, 3, 1).plusDays(i * 7L);
            c.crearReserva(e.ana(), e.h101(), in, in.plusDays(2), c.seleccionarPolitica("flexible"));
        }
        System.out.println("\n-- Ana (cliente frecuente) vs Luis (nuevo), suite en temporada alta:");
        Reserva rAna = c.crearReserva(e.ana(), e.h201(), ENTRADA, SALIDA, c.seleccionarPolitica("moderada"));
        Reserva rLuis = c.crearReserva(e.luis(), e.h301(), ENTRADA, SALIDA, c.seleccionarPolitica("flexible"));
        System.out.println("   " + rAna);
        System.out.println("   " + rLuis);

        System.out.println("\n-- Check-in / check-out y asignación de limpieza");
        c.checkIn(rAna);
        System.out.println("   Habitación " + rAna.getHabitacion().getNumero() + " ocupada: " + rAna.getHabitacion().isOcupada());
        System.out.println("   Limpieza asignada a: " + c.checkOut(rAna));
        c.checkIn(rLuis);
        System.out.println("   Limpieza asignada a: " + c.checkOut(rLuis));
        System.out.println("   Carga de trabajo: " + c.consultarCargaTrabajo());

        System.out.println("\n-- Historial de Ana: " + c.consultarHistorial(e.ana()).size() + " reservas");

        System.out.println("\n-- Informe de ocupación 01/12/2026 - 01/01/2027");
        new GeneradorInformeOcupacion()
                .ocupacionPorTipo(c.getHabitaciones(), c.getReservas(), LocalDate.of(2026, 12, 1), LocalDate.of(2027, 1, 1))
                .forEach((tipo, pct) -> System.out.printf("   %-9s %.1f%%%n", tipo, pct));

        double ingresos = new GeneradorInformeIngresos()
                .ingresosPorPeriodo(c.getReservas(), LocalDate.of(2026, 12, 1), LocalDate.of(2027, 1, 1));
        System.out.println("-- Ingresos diciembre 2026: " + Formato.soles(ingresos));
        System.out.println("-- Ingresos marzo 2026: " + Formato.soles(new GeneradorInformeIngresos()
                .ingresosPorPeriodo(c.getReservas(), LocalDate.of(2026, 3, 1), LocalDate.of(2026, 4, 1))));
    }
}
