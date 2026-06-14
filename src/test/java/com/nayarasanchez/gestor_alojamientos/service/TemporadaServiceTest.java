
package com.nayarasanchez.gestor_alojamientos.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TemporadaServiceTest {

    @Test
    void PUNI05_detectarTemporadasSolapadas() {
        LocalDate inicioTemporadaExistente = LocalDate.of(2026, 7, 1);
        LocalDate finTemporadaExistente = LocalDate.of(2026, 7, 15);

        LocalDate inicioNuevaTemporada = LocalDate.of(2026, 7, 10);
        LocalDate finNuevaTemporada = LocalDate.of(2026, 7, 20);

        boolean haySolapamiento =
                !inicioNuevaTemporada.isAfter(finTemporadaExistente)
                        && !finNuevaTemporada.isBefore(inicioTemporadaExistente);

        assertTrue(haySolapamiento);
    }

    @Test
    void PUNI06_permitirTemporadasConsecutivas() {
        LocalDate inicioTemporadaExistente = LocalDate.of(2026, 7, 1);
        LocalDate finTemporadaExistente = LocalDate.of(2026, 7, 15);

        LocalDate inicioNuevaTemporada = LocalDate.of(2026, 7, 16);
        LocalDate finNuevaTemporada = LocalDate.of(2026, 7, 31);

        boolean haySolapamiento =
                !inicioNuevaTemporada.isAfter(finTemporadaExistente)
                        && !finNuevaTemporada.isBefore(inicioTemporadaExistente);

        assertFalse(haySolapamiento);
    }

    @Test
    void PUNI07_rechazarFechaFinAnteriorAFechaInicio() {
        LocalDate fechaInicio = LocalDate.of(2026, 8, 10);
        LocalDate fechaFin = LocalDate.of(2026, 8, 1);

        boolean fechasValidas = fechaFin.isAfter(fechaInicio);

        assertFalse(fechasValidas);
    }

    @Test
    void PUNI07_rechazarFechaFinIgualAFechaInicio() {
        LocalDate fechaInicio = LocalDate.of(2026, 8, 10);
        LocalDate fechaFin = LocalDate.of(2026, 8, 10);

        boolean fechasValidas = fechaFin.isAfter(fechaInicio);

        assertFalse(fechasValidas);
    }
}