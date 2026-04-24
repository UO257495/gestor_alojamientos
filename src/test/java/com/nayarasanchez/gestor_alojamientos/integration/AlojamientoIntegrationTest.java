package com.nayarasanchez.gestor_alojamientos.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
import com.nayarasanchez.gestor_alojamientos.repository.AlojamientoRepository;

@SpringBootTest
@Transactional
class AlojamientoIntegrationTest {

    @Autowired
    private AlojamientoRepository alojamientoRepository;

    @Test
    void guardarAlojamiento_debePersistirCorrectamente() {
        Alojamiento alojamiento = new Alojamiento();
        alojamiento.setNombre("Casa rural de prueba");
        alojamiento.setDireccion("Peón, Villaviciosa");
        alojamiento.setDescripcion("Alojamiento creado durante una prueba de integración.");
        alojamiento.setCapacidad(4);
        alojamiento.setTarifaBase(80.00);
        alojamiento.setLatitud(43.4812);
        alojamiento.setLongitud(-5.4351);

        Alojamiento guardado = alojamientoRepository.save(alojamiento);

        assertNotNull(guardado.getId());
        assertEquals("Casa rural de prueba", guardado.getNombre());
        assertEquals(4, guardado.getCapacidad());
    }
}