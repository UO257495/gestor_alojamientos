package com.nayarasanchez.gestor_alojamientos.service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.MessageResolver;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.PropertiesMessageResolver;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nayarasanchez.gestor_alojamientos.dto.form.UsuarioForm;
import com.nayarasanchez.gestor_alojamientos.model.Rol;
import com.nayarasanchez.gestor_alojamientos.model.Usuario;
import com.nayarasanchez.gestor_alojamientos.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarClientes(){
        return usuarioRepository.findAllByRol(Rol.CLIENTE);
    }

    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> obtenerUsuarioPorEmail(String email){
        return usuarioRepository.findByEmail(email);
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public boolean comprobarEmailEnUso(String email) {
        return usuarioRepository.comprobarEmailEnUso(email);
    }

     /**
     * Comprueba si la contraseña de usuario cumple la política de calidad de contraseñas.
     * Implementación obtenida de https://dzone.com/articles/spring-boot-custom-password-validator-using-passay.
     * @param password
     * @return
     */
    @SneakyThrows
    public boolean comprobarPoliticaCalidadPassword(String password) {

        // Mensajes de los errores de validación
        Properties props = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("passay.properties");
        props.load(inputStream);
        MessageResolver resolver = new PropertiesMessageResolver(props);

        PasswordValidator validator = new PasswordValidator(resolver, Arrays.asList(
            // Longitud mínima entre 8 y máxima de 16
            new LengthRule(8, 16),
            // Al menos una mayúscula
            new CharacterRule(EnglishCharacterData.UpperCase, 1),
            // Al menos una letra minúscula
            new CharacterRule(EnglishCharacterData.LowerCase, 1),
            // Al menos un número
            new CharacterRule(EnglishCharacterData.Digit, 1),
            // Al menos un caracter especial (símbolo)
            //new CharacterRule(EnglishCharacterData.Special, 1),
            // Sin espacios en blanco
            new WhitespaceRule()
        ));

        RuleResult result = validator.validate(new PasswordData(password));

        if (result.isValid()) {
            return true;
        } else {
            List<String> errores = validator.getMessages(result);
            String mensajeErrores = String.join(",", errores);
            log.error("La contraseña no cumple la política de calidad: "+ mensajeErrores);
            return false;
        }
    }

    public Usuario crearUsuario(UsuarioForm form, String password, Authentication auth) {
        Usuario usuario = new Usuario();
        usuario.setNombre(form.getNombre());
        usuario.setEmail(form.getEmail());
        usuario.setDni(form.getDni());
        usuario.setTelefono(form.getTelefono());
        usuario.setPassword(passwordEncoder.encode(password));
        // Si el usuario no está logueado o no tiene permisos para cambiar rol
        if (auth == null || !auth.isAuthenticated() || 
            auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_PROPIETARIO"))) {
            usuario.setRol(Rol.CLIENTE);
        } else {
            usuario.setRol(form.getRol());
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario editarUsuario(Usuario usuarioExistente, UsuarioForm form, String password) {
        usuarioExistente.setNombre(form.getNombre());
        usuarioExistente.setEmail(form.getEmail());
        usuarioExistente.setDni(form.getDni());
        usuarioExistente.setTelefono(form.getTelefono());
        usuarioExistente.setRol(form.getRol());
        if (password != null && !password.isEmpty()) {
            usuarioExistente.setPassword(passwordEncoder.encode(password));
        }
        return usuarioRepository.save(usuarioExistente);
    }
}
