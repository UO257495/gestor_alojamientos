package com.nayarasanchez.gestor_alojamientos.cucumber.steps;


import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TemporadaSteps {

    private LocalDate inicioExistente;
    private LocalDate finExistente;
    private boolean temporadaValida;

    @Given("existe una temporada del {int} de julio de {int} al {int} de julio de {int}")
    public void existeTemporada(int diaInicio, int anioInicio, int diaFin, int anioFin) {
        inicioExistente = LocalDate.of(anioInicio, 7, diaInicio);
        finExistente = LocalDate.of(anioFin, 7, diaFin);
    }

    @When("se crea otra temporada del {int} de julio de {int} al {int} de julio de {int}")
    public void seCreaOtraTemporada(int diaInicio, int anioInicio, int diaFin, int anioFin) {
        LocalDate inicioNueva = LocalDate.of(anioInicio, 7, diaInicio);
        LocalDate finNueva = LocalDate.of(anioFin, 7, diaFin);

        boolean solapada = !inicioNueva.isAfter(finExistente) && !finNueva.isBefore(inicioExistente);

        temporadaValida = !solapada && finNueva.isAfter(inicioNueva);
    }

    @Then("el sistema permite crear la temporada")
    public void sistemaPermiteCrearTemporada() {
        assertTrue(temporadaValida);
    }

    @Then("el sistema rechaza la temporada")
    public void sistemaRechazaTemporada() {
        assertFalse(temporadaValida);
    }
}