package com.nayarasanchez.gestor_alojamientos.dto.form;

import com.nayarasanchez.gestor_alojamientos.model.Rol;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioForm {

    private Long id;

    private Rol rol;

    @NotBlank(message = "{validation.obligatorio}")
    private String dni; 

    @NotBlank(message = "{validation.obligatorio}")
    private String nombre;

    @NotBlank(message = "{validation.obligatorio}")
    private String telefono;

    @NotBlank(message = "{validation.obligatorio}")
    private String email;

    private String password;

}
