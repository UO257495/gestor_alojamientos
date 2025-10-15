package com.nayarasanchez.gestor_alojamientos.dto.form;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlojamientoForm {

    private Long id;

    @NotNull(message = "{validation.obligatorio}")
    private String nombre;

    @NotNull(message = "{validation.obligatorio}")
    private String direccion;

    @NotNull(message = "{validation.obligatorio}")
    private String descripcion;

    @NotNull(message = "{validation.obligatorio}")
    private MultipartFile foto; 
    
    @NotNull(message = "{validation.obligatorio}")
    private Double tarifaBase;

    @NotNull(message = "{validation.obligatorio}")
    private Double latitud;
    
    @NotNull(message = "{validation.obligatorio}")
    private Double longitud; 

    @NotNull(message = "{validation.obligatorio}")
    private Integer capacidad; 


}