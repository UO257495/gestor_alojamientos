package com.nayarasanchez.gestor_alojamientos.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TemporadaServiceTest {

    @Test
    void temporadasSolapadas_debenDetectarse() {
        LocalDate inicio1 = LocalDate.of(2026, 7, 1);
        LocalDate fin1 = LocalDate.of(2026, 7, 15);

        LocalDate inicio2 = LocalDate.of(2026, 7, 10);
        LocalDate fin2 = LocalDate.of(2026, 7, 20);

        boolean solapadas = !inicio2.isAfter(fin1) && !fin2.isBefore(inicio1);

        assertTrue(solapadas);
    }

    @Test
    void temporadasNoSolapadas_debenPermitirse() {
        LocalDate inicio1 = LocalDate.of(2026, 7, 1);
        LocalDate fin1 = LocalDate.of(2026, 7, 15);

        LocalDate inicio2 = LocalDate.of(2026, 7, 16);
        LocalDate fin2 = LocalDate.of(2026, 7, 31);

        boolean solapadas = !inicio2.isAfter(fin1) && !fin2.isBefore(inicio1);

        assertFalse(solapadas);
    }

    @Test
    void temporadaConFechaFinAnterior_debeSerInvalida() {
        LocalDate inicio = LocalDate.of(2026, 8, 10);
        LocalDate fin = LocalDate.of(2026, 8, 1);

        assertFalse(fin.isAfter(inicio));
    }
}
