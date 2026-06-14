package com.nayarasanchez.gestor_alojamientos.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.nayarasanchez.gestor_alojamientos.dto.form.UsuarioForm;
import com.nayarasanchez.gestor_alojamientos.model.Rol;
import com.nayarasanchez.gestor_alojamientos.model.Usuario;
import com.nayarasanchez.gestor_alojamientos.repository.UsuarioRepository;
import com.nayarasanchez.gestor_alojamientos.service.UsuarioService;

@SpringBootTest
@Transactional
class UsuarioIntegrationTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void PI02_registrarNuevoCliente() {
        UsuarioForm form = new UsuarioForm();
        form.setNombre("Cliente Prueba");
        form.setDni("87654321B");
        form.setTelefono("600000001");
        form.setEmail("cliente" + System.nanoTime() + "@test.com");

        Usuario guardado = usuarioService.crearUsuario(form, "Password123", null);

        Usuario encontrado = usuarioRepository.findById(guardado.getId()).orElseThrow();

        assertNotNull(encontrado.getId());
        assertEquals(Rol.CLIENTE, encontrado.getRol());
        assertEquals(form.getEmail(), encontrado.getEmail());
        assertNotEquals("Password123", encontrado.getPassword());
        assertTrue(encontrado.getPassword().startsWith("$2a$")
                || encontrado.getPassword().startsWith("$2b$")
                || encontrado.getPassword().startsWith("$2y$"));
    }
}