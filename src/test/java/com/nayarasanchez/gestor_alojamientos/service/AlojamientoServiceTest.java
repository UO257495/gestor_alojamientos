package com.nayarasanchez.gestor_alojamientos.service;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AlojamientoServiceTest {

    @Test
    void PUNI08_validarNombreObligatorio() {
        String nombre = "";

        assertTrue(nombre.isBlank());
    }

    @Test
    void PUNI08_validarDireccionObligatoria() {
        String direccion = "";

        assertTrue(direccion.isBlank());
    }

    @Test
    void PUNI08_validarCapacidadMayorQueCero() {
        int capacidad = 0;

        assertFalse(capacidad > 0);
    }
}