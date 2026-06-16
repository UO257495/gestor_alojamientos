package com.nayarasanchez.gestor_alojamientos.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import com.nayarasanchez.gestor_alojamientos.dto.form.AlojamientoForm;
import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
import com.nayarasanchez.gestor_alojamientos.repository.AlojamientoRepository;
import com.nayarasanchez.gestor_alojamientos.service.AlojamientoService;
import com.nayarasanchez.gestor_alojamientos.service.SupabaseStorageService;

@SpringBootTest
@Transactional
class AlojamientoIntegrationTest {

    @Autowired
    private AlojamientoService alojamientoService;

    @Autowired
    private AlojamientoRepository alojamientoRepository;

    @MockitoBean
    private SupabaseStorageService storageService;

    @Test
    void PI01_crearYPersistirAlojamiento() throws Exception {
        AlojamientoForm form = new AlojamientoForm();
        form.setNombre("Casa rural de prueba");
        form.setDireccion("Peón, Villaviciosa");
        form.setDescripcion("Alojamiento creado durante una prueba de integración.");
        form.setCapacidad(4);
        form.setTarifaBase(80.00);
        form.setLatitud(43.4812);
        form.setLongitud(-5.4351);

        Alojamiento guardado = alojamientoService.crearOActualizar(form);

        Alojamiento encontrado = alojamientoRepository.findById(guardado.getId()).orElseThrow();

        assertNotNull(encontrado.getId());
        assertEquals("Casa rural de prueba", encontrado.getNombre());
        assertEquals("Peón, Villaviciosa", encontrado.getDireccion());
        assertEquals(4, encontrado.getCapacidad());
    }
}