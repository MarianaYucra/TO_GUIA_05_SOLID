package com.unsa.hotel.precios;

import com.unsa.hotel.modelo.Cliente;
import com.unsa.hotel.modelo.Habitacion;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * SRP (Ejercicio 6): única responsabilidad = calcular el precio de una estadía.
 * OCP: las promociones se inyectan; no hay if/else por tipo de promoción.
 * Regla de negocio: se aplica la promoción más ventajosa (no se acumulan).
 */
public class CalculadoraPrecioHabitacion {

    private final List<Promocion> promociones = new ArrayList<>();

    public CalculadoraPrecioHabitacion(List<Promocion> promocionesIniciales) {
        promociones.addAll(promocionesIniciales);
    }

    public void agregarPromocion(Promocion promocion) { promociones.add(promocion); }

    public double calcularSubtotal(Habitacion habitacion, LocalDate entrada, LocalDate salida) {
        long noches = ChronoUnit.DAYS.between(entrada, salida);
        return noches * habitacion.getPrecioPorNoche();
    }

    public double calcularDescuento(Habitacion habitacion, Cliente cliente, LocalDate entrada, LocalDate salida) {
        double subtotal = calcularSubtotal(habitacion, entrada, salida);
        return promociones.stream()
                .filter(p -> p.aplica(habitacion, cliente, entrada, salida))
                .mapToDouble(p -> p.calcularDescuento(subtotal))
                .max()
                .orElse(0);
    }

    public double calcularPrecioFinal(Habitacion habitacion, Cliente cliente, LocalDate entrada, LocalDate salida) {
        return calcularSubtotal(habitacion, entrada, salida)
                - calcularDescuento(habitacion, cliente, entrada, salida);
    }
}
