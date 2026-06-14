package com.nayarasanchez.gestor_alojamientos.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.nayarasanchez.gestor_alojamientos.dto.form.TemporadaForm;
import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
import com.nayarasanchez.gestor_alojamientos.model.Temporada;
import com.nayarasanchez.gestor_alojamientos.repository.AlojamientoRepository;
import com.nayarasanchez.gestor_alojamientos.repository.TemporadaRepository;
import com.nayarasanchez.gestor_alojamientos.service.TemporadaService;

@SpringBootTest
@Transactional
class TemporadaIntegrationTest {

    @Autowired
    private TemporadaService temporadaService;

    @Autowired
    private TemporadaRepository temporadaRepository;

    @Autowired
    private AlojamientoRepository alojamientoRepository;

    @Test
    void PI05_crearTemporadaAsociadaAAlojamiento() {
        Alojamiento alojamiento = crearAlojamiento();

        TemporadaForm form = new TemporadaForm();
        form.setNombre("Temporada alta");
        form.setFechaInicio(LocalDate.of(2026, 7, 1));
        form.setFechaFin(LocalDate.of(2026, 8, 31));
        form.setPrecio(120.00);
        form.setAlojamiento(alojamiento.getId());

        Temporada guardada = temporadaService.crearOActualizar(form);

        Temporada encontrada = temporadaRepository.findById(guardada.getId()).orElseThrow();

        assertNotNull(encontrada.getId());
        assertEquals("Temporada alta", encontrada.getNombre());
        assertEquals(120.00, encontrada.getPrecio());
        assertEquals(alojamiento.getId(), encontrada.getAlojamiento().getId());
    }

    private Alojamiento crearAlojamiento() {
        Alojamiento alojamiento = new Alojamiento();
        alojamiento.setNombre("Casa rural temporada");
        alojamiento.setDireccion("Peón, Villaviciosa");
        alojamiento.setDescripcion("Alojamiento para prueba de temporada.");
        alojamiento.setCapacidad(4);
        alojamiento.setTarifaBase(80.00);
        alojamiento.setLatitud(43.4812);
        alojamiento.setLongitud(-5.4351);
        return alojamientoRepository.save(alojamiento);
    }
}