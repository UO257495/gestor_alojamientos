package com.nayarasanchez.gestor_alojamientos.dto.form;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TemporadaForm {
    private Long id;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Double precio;
    private Long alojamientoId;
}
