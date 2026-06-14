package com.nayarasanchez.gestor_alojamientos.cucumber.steps;

import static org.junit.jupiter.api.Assertions.*;

import com.nayarasanchez.gestor_alojamientos.dto.form.UsuarioForm;
import com.nayarasanchez.gestor_alojamientos.model.Rol;
import com.nayarasanchez.gestor_alojamientos.model.Usuario;
import com.nayarasanchez.gestor_alojamientos.repository.UsuarioRepository;
import com.nayarasanchez.gestor_alojamientos.service.UsuarioService;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.springframework.beans.factory.annotation.Autowired;

public class RegistroSteps {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private UsuarioForm form;
    private Usuario usuarioCreado;
    private String email;

    @Given("el usuario no está registrado en la plataforma")
    public void usuarioNoRegistrado() {
        String identificador = String.valueOf(System.nanoTime());

        email = "registro" + identificador + "@test.com";

        form = new UsuarioForm();
        form.setNombre("Cliente Registro");
        form.setDni(identificador.substring(identificador.length() - 8) + "R");
        form.setTelefono("600000001");
        form.setEmail(email);

        assertTrue(usuarioRepository.findByEmail(email).isEmpty());
    }

    @When("completa el formulario de registro con datos válidos")
    public void completaFormularioRegistro() {
        usuarioCreado = usuarioService.crearUsuario(form, "Password123", null);
    }

    @Then("la cuenta se crea correctamente con rol CLIENTE")
    public void cuentaCreadaConRolCliente() {
        Usuario encontrado = usuarioRepository.findById(usuarioCreado.getId()).orElseThrow();

        assertEquals(Rol.CLIENTE, encontrado.getRol());
        assertEquals(email, encontrado.getEmail());
        assertNotEquals("Password123", encontrado.getPassword());
    }
}