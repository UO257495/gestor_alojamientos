package com.nayarasanchez.gestor_alojamientos.dto.form;

import com.nayarasanchez.gestor_alojamientos.model.Rol;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;
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
    @Pattern(regexp = "^[0-9]{8}[A-Za-z]$|^[XYZxyz][0-9]{7}[A-Za-z]$", message = "{validation.dni.formato}")
    private String dni; 

    @NotBlank(message = "{validation.obligatorio}")
    private String nombre;

    @NotBlank(message = "{validation.obligatorio}")
    @Pattern(regexp = "^[6789]\\d{8}$", message = "{validation.telefono.formato}")
    private String telefono;

    @NotBlank(message = "{validation.obligatorio}")
    @Email(message = "{validation.email.formato}")
    private String email;

    private String password;

    private Boolean aceptaPrivacidad;

}
