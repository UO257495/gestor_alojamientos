package com.nayarasanchez.gestor_alojamientos.service;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class AlojamientoServiceTest {

    @Test
    void capacidadDebeSerMayorQueCero() {
        int capacidad = 4;

        assertTrue(capacidad > 0);
    }

    @Test
    void nombreAlojamientoNoDebeEstarVacio() {
        String nombre = "Casa rural Peón";

        assertFalse(nombre.isBlank());
    }

    @Test
    void direccionAlojamientoNoDebeEstarVacia() {
        String direccion = "Peón, Villaviciosa";

        assertFalse(direccion.isBlank());
    }
}