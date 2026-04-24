package com.nayarasanchez.gestor_alojamientos.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ReservaServiceTest {

    @Test
    void calcularNumeroNoches_noDebeCobrarDiaSalida() {
        LocalDate fechaInicio = LocalDate.of(2026, 7, 1);
        LocalDate fechaFin = LocalDate.of(2026, 7, 5);

        long noches = fechaFin.toEpochDay() - fechaInicio.toEpochDay();

        assertEquals(4, noches);
    }

    @Test
    void calcularPrecioTotal_debeMultiplicarPrecioPorNoches() {
        BigDecimal precioNoche = new BigDecimal("80.00");
        int noches = 4;

        BigDecimal total = precioNoche.multiply(BigDecimal.valueOf(noches));

        assertEquals(new BigDecimal("320.00"), total);
    }

    @Test
    void fechasInvalidas_debeDetectarFechaFinAnteriorAFechaInicio() {
        LocalDate fechaInicio = LocalDate.of(2026, 7, 10);
        LocalDate fechaFin = LocalDate.of(2026, 7, 5);

        assertFalse(fechaFin.isAfter(fechaInicio));
    }

    @Test
    void reservaConMismaFechaInicioYFin_debeSerInvalida() {
        LocalDate fechaInicio = LocalDate.of(2026, 7, 10);
        LocalDate fechaFin = LocalDate.of(2026, 7, 10);

        assertFalse(fechaFin.isAfter(fechaInicio));
    }
}