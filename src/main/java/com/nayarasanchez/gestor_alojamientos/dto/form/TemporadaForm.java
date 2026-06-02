package com.nayarasanchez.gestor_alojamientos.dto.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Data
public class TemporadaForm {
    private Long id;

    private String nombre;

    @DateTimeFormat(pattern = "dd/MM/yyyy") 
    private LocalDate fechaInicio;

    @DateTimeFormat(pattern = "dd/MM/yyyy") 
    private LocalDate fechaFin;

    private Double precio;
    
    private Long alojamiento;

    @AssertTrue(message = "La fecha fin debe ser igual o posterior a la fecha de inicio")
    public boolean isRangoFechasValido() {
        if (fechaInicio == null || fechaFin == null) {
            return true;
        }

        return !fechaFin.isBefore(fechaInicio);
    }
}
