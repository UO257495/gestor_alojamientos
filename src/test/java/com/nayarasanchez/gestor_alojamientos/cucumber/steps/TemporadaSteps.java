package com.nayarasanchez.gestor_alojamientos.cucumber.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;

import com.nayarasanchez.gestor_alojamientos.dto.form.TemporadaForm;
import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
import com.nayarasanchez.gestor_alojamientos.model.Temporada;
import com.nayarasanchez.gestor_alojamientos.repository.AlojamientoRepository;
import com.nayarasanchez.gestor_alojamientos.repository.TemporadaRepository;
import com.nayarasanchez.gestor_alojamientos.service.TemporadaService;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TemporadaSteps {

    @Autowired
    private TemporadaService temporadaService;

    @Autowired
    private TemporadaRepository temporadaRepository;

    @Autowired
    private AlojamientoRepository alojamientoRepository;

    private Alojamiento alojamiento;
    private Temporada temporadaCreada;
    private Exception excepcion;

    @Given("existe un alojamiento para gestionar temporadas")
    public void existeAlojamientoParaGestionarTemporadas() {
        alojamiento = crearAlojamiento();
    }

    @When("el propietario crea una temporada válida")
    public void propietarioCreaTemporadaValida() {
        TemporadaForm form = new TemporadaForm();
        form.setNombre("Temporada alta");
        form.setFechaInicio(LocalDate.of(2026, 7, 1));
        form.setFechaFin(LocalDate.of(2026, 8, 31));
        form.setPrecio(120.00);
        form.setAlojamiento(alojamiento.getId());

        temporadaCreada = temporadaService.crearOActualizar(form);
    }

    @Then("la temporada queda registrada correctamente")
    public void temporadaRegistradaCorrectamente() {
        Temporada encontrada = temporadaRepository.findById(temporadaCreada.getId()).orElseThrow();

        assertEquals("Temporada alta", encontrada.getNombre());
        assertEquals(alojamiento.getId(), encontrada.getAlojamiento().getId());
    }

    @Given("existe una temporada previa para el alojamiento")
    public void existeTemporadaPreviaParaAlojamiento() {
        alojamiento = crearAlojamiento();

        TemporadaForm form = new TemporadaForm();
        form.setNombre("Temporada previa");
        form.setFechaInicio(LocalDate.of(2026, 7, 1));
        form.setFechaFin(LocalDate.of(2026, 7, 15));
        form.setPrecio(100.00);
        form.setAlojamiento(alojamiento.getId());

        temporadaService.crearOActualizar(form);
    }

    @When("el propietario intenta crear una temporada solapada")
    public void propietarioIntentaCrearTemporadaSolapada() {
        try {
            TemporadaForm form = new TemporadaForm();
            form.setNombre("Temporada solapada");
            form.setFechaInicio(LocalDate.of(2026, 7, 10));
            form.setFechaFin(LocalDate.of(2026, 7, 20));
            form.setPrecio(120.00);
            form.setAlojamiento(alojamiento.getId());

            temporadaService.crearOActualizar(form);
        } catch (Exception e) {
            excepcion = e;
        }
    }

    @Then("el sistema muestra un error y no guarda la temporada")
    public void sistemaMuestraErrorYNoGuardaTemporada() {
        assertNotNull(excepcion);
        assertTrue(excepcion instanceof IllegalArgumentException);
    }

    private Alojamiento crearAlojamiento() {
        Alojamiento alojamiento = new Alojamiento();
        alojamiento.setNombre("Casa rural temporadas");
        alojamiento.setDireccion("Peón, Villaviciosa");
        alojamiento.setDescripcion("Alojamiento para pruebas Cucumber.");
        alojamiento.setCapacidad(4);
        alojamiento.setTarifaBase(80.00);
        alojamiento.setLatitud(43.4812);
        alojamiento.setLongitud(-5.4351);

        return alojamientoRepository.save(alojamiento);
    }
}