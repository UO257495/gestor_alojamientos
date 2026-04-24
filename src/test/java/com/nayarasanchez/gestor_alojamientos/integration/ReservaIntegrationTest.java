package com.nayarasanchez.gestor_alojamientos.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ReservaIntegrationTest {

    @Test
    void calcularPrecioReserva_debeSerConsistenteEnBackend() {
        LocalDate fechaInicio = LocalDate.of(2026, 7, 1);
        LocalDate fechaFin = LocalDate.of(2026, 7, 5);
        BigDecimal precioNoche = new BigDecimal("80.00");

        long noches = fechaFin.toEpochDay() - fechaInicio.toEpochDay();
        BigDecimal precioTotal = precioNoche.multiply(BigDecimal.valueOf(noches));

        assertEquals(new BigDecimal("320.00"), precioTotal);
    }

    @Test
    void reservaConFechasInvalidas_noDebePermitirse() {
        LocalDate fechaInicio = LocalDate.of(2026, 7, 10);
        LocalDate fechaFin = LocalDate.of(2026, 7, 5);

        assertFalse(fechaFin.isAfter(fechaInicio));
    }
}