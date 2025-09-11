package com.nayarasanchez.gestor_alojamientos.dto.form;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BusquedaAlojamientoForm {
    private String nombre;
    private String ubicacion;
    private Integer personas;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}