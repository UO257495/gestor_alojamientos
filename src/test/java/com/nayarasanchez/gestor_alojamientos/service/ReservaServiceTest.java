package com.nayarasanchez.gestor_alojamientos.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ReservaServiceTest {

    @Test
    void PUNI01_calcularNumeroNoches_sinCobrarDiaSalida() {
        LocalDate fechaEntrada = LocalDate.of(2026, 7, 1);
        LocalDate fechaSalida = LocalDate.of(2026, 7, 5);

        long noches = fechaSalida.toEpochDay() - fechaEntrada.toEpochDay();

        assertEquals(4, noches);
    }

    @Test
    void PUNI02_calcularPrecioTotalReserva() {
        BigDecimal precioPorNoche = new BigDecimal("80.00");
        int numeroNoches = 4;

        BigDecimal total = precioPorNoche.multiply(BigDecimal.valueOf(numeroNoches));

        assertEquals(new BigDecimal("320.00"), total);
    }

    @Test
    void PUNI03_rechazarFechaSalidaAnteriorAFechaEntrada() {
        LocalDate fechaEntrada = LocalDate.of(2026, 7, 10);
        LocalDate fechaSalida = LocalDate.of(2026, 7, 5);

        boolean fechasValidas = fechaSalida.isAfter(fechaEntrada);

        assertFalse(fechasValidas);
    }

    @Test
    void PUNI03_rechazarFechaSalidaIgualAFechaEntrada() {
        LocalDate fechaEntrada = LocalDate.of(2026, 7, 10);
        LocalDate fechaSalida = LocalDate.of(2026, 7, 10);

        boolean fechasValidas = fechaSalida.isAfter(fechaEntrada);

        assertFalse(fechasValidas);
    }

    @Test
    void PUNI04_detectarSolapamientoReservas() {
        LocalDate reservaExistenteEntrada = LocalDate.of(2026, 7, 10);
        LocalDate reservaExistenteSalida = LocalDate.of(2026, 7, 15);

        LocalDate nuevaReservaEntrada = LocalDate.of(2026, 7, 12);
        LocalDate nuevaReservaSalida = LocalDate.of(2026, 7, 18);

        boolean haySolapamiento =
                nuevaReservaEntrada.isBefore(reservaExistenteSalida)
                        && nuevaReservaSalida.isAfter(reservaExistenteEntrada);

        assertTrue(haySolapamiento);
    }
}