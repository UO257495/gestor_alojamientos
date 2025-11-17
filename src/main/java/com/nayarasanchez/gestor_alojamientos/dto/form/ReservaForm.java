package com.nayarasanchez.gestor_alojamientos.dto.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.nayarasanchez.gestor_alojamientos.model.EstadoPago;
import com.nayarasanchez.gestor_alojamientos.model.EstadoReserva;
import com.nayarasanchez.gestor_alojamientos.model.FormaPago;
import com.nayarasanchez.gestor_alojamientos.model.Valoracion;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservaForm {
    
    private Long id;

    @NotNull(message = "Debe seleccionar la fecha de inicio")
    @DateTimeFormat(pattern = "dd/MM/yyyy") 
    private LocalDate fechaInicio;

    @NotNull(message = "Debe seleccionar la fecha de fin")
    @DateTimeFormat(pattern = "dd/MM/yyyy") 
    private LocalDate fechaFin;

    private int numeroPersonas;

  
    private EstadoReserva estado;

    @NotNull
    private Long clienteId;
    
    @NotNull
    private Long alojamientoId;

    @NotNull
    private Double precioTotal; //Total calculado de precio de tarifa base del alojamiento + incremento de la temporada

    @NotNull(message = "Debe seleccionar la forma de pago")
    private FormaPago formaPago;

   
    private EstadoPago estadoPago;

    private Valoracion valoracion;

}
