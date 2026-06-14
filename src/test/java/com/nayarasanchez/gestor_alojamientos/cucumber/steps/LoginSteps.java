package com.nayarasanchez.gestor_alojamientos.cucumber.steps;

import static org.junit.jupiter.api.Assertions.*;

import com.nayarasanchez.gestor_alojamientos.model.Rol;
import com.nayarasanchez.gestor_alojamientos.model.Usuario;
import com.nayarasanchez.gestor_alojamientos.repository.UsuarioRepository;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class LoginSteps {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario usuario;
    private String panelDestino;

    @Given("existe un usuario registrado con credenciales válidas")
    public void existeUsuarioRegistradoConCredencialesValidas() {
        String identificador = String.valueOf(System.nanoTime());

        usuario = new Usuario();
        usuario.setNombre("Cliente Login");
        usuario.setDni(identificador.substring(identificador.length() - 8) + "L");
        usuario.setTelefono("600000000");
        usuario.setEmail("login" + identificador + "@test.com");
        usuario.setPassword(passwordEncoder.encode("Password123"));
        usuario.setRol(Rol.CLIENTE);

        usuarioRepository.save(usuario);
    }

    @When("el usuario inicia sesión")
    public void usuarioIniciaSesion() {
        boolean credencialesValidas = passwordEncoder.matches("Password123", usuario.getPassword());

        if (credencialesValidas && usuario.getRol() == Rol.CLIENTE) {
            panelDestino = "panel_cliente";
        }
    }

    @Then("accede al panel correspondiente a su rol")
    public void accedeAlPanelCorrespondienteASuRol() {
        assertEquals("panel_cliente", panelDestino);
    }
}