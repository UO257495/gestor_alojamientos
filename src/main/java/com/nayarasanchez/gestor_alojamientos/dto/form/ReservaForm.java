package com.nayarasanchez.gestor_alojamientos.dto.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.nayarasanchez.gestor_alojamientos.model.EstadoReserva;
import com.nayarasanchez.gestor_alojamientos.model.FormaPago;

import lombok.Data;

@Data
public class ReservaForm {
    
    private Long id;

    @DateTimeFormat(pattern = "dd/MM/yyyy") 
    private LocalDate fechaInicio;

    @DateTimeFormat(pattern = "dd/MM/yyyy") 
    private LocalDate fechaFin;

    private int numeroPersonas;

    private EstadoReserva estado;

    private Long clienteId;
    
    private Long alojamientoId;

    private Double precioTotal; //Total calculado de precio de tarifa base del alojamiento + incremento de la temporada

    private FormaPago formaPago;
}
