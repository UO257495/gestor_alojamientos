package com.nayarasanchez.gestor_alojamientos.cucumber.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ReservaSteps {

    private BigDecimal precioBase;
    private BigDecimal total;
    private boolean reservaValida;

    @Given("existe un alojamiento con precio base de {int} euros")
    public void existeAlojamientoConPrecioBase(int precio) {
        this.precioBase = BigDecimal.valueOf(precio);
    }

    @When("el cliente realiza una reserva del {int} de julio de {int} al {int} de julio de {int}")
    public void clienteRealizaReserva(int diaInicio, int anioInicio, int diaFin, int anioFin) {
        LocalDate fechaInicio = LocalDate.of(anioInicio, 7, diaInicio);
        LocalDate fechaFin = LocalDate.of(anioFin, 7, diaFin);

        reservaValida = fechaFin.isAfter(fechaInicio);

        if (reservaValida) {
            long noches = fechaFin.toEpochDay() - fechaInicio.toEpochDay();
            total = precioBase.multiply(BigDecimal.valueOf(noches));
        }
    }

    @Then("el sistema calcula un total de {int} euros")
    public void sistemaCalculaTotal(int totalEsperado) {
        assertEquals(BigDecimal.valueOf(totalEsperado), total);
    }

    @Then("el sistema rechaza la reserva")
    public void sistemaRechazaReserva() {
        assertFalse(reservaValida);
    }
}
