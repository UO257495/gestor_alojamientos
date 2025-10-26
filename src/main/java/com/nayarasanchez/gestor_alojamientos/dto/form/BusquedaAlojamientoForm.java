package com.nayarasanchez.gestor_alojamientos.dto.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class BusquedaAlojamientoForm {
    
    private String nombre;

    private String ubicacion;
    
    private Integer personas;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;

}
