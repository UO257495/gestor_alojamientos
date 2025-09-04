package com.nayarasanchez.gestor_alojamientos.controller;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
import org.springframework.beans.BeanUtils;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nayarasanchez.gestor_alojamientos.dto.form.UsuarioForm;
import com.nayarasanchez.gestor_alojamientos.dto.view.MensajeUsuario;
import com.nayarasanchez.gestor_alojamientos.model.Rol;
import com.nayarasanchez.gestor_alojamientos.model.Usuario;
import com.nayarasanchez.gestor_alojamientos.repository.UsuarioRepository;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/gestion/usuarios")
@RequiredArgsConstructor
@Slf4j
public class GestionUsuariosController {
    
    private final MessageSource messageSource;

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;


    @GetMapping("/lista")
    public String lista(Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "gestion/usuarios/lista";
    }

    @GetMapping("/detalle")
    public String detalle(@RequestParam("id") Optional<Long> id, Model model, Authentication auth) {
         // Comprueba si el usuario logueado es ADMIN
        // boolean esAdmin = auth.getAuthorities().stream()
        //                     .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        // model.addAttribute("esAdmin", esAdmin);
        Usuario usuario = new Usuario();
        if (id.isPresent()) {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(id.get());
            usuario = usuarioOpt.orElse(new Usuario());
        }

        model.addAttribute("roles", Rol.values());
        model.addAttribute("usuario", usuario);

        return "gestion/usuarios/detalle"; 
    }

    @PostMapping("/nuevo")
    public String nuevo(@RequestParam("password") String password, @RequestParam("confirmarPassword") String confirmarPassword,
            @Valid @ModelAttribute("usuario") UsuarioForm usuarioForm, BindingResult result, Model model, Locale locale, 
            RedirectAttributes redirectAttributes) throws Exception {

        if (result.hasErrors()){
            model.addAttribute("roles", Rol.values());
            return "gestion/usuarios/detalle";
        }
                
        // Comprueba si el email del nuevo usuario ya está en uso
        if (usuarioRepository.comprobarEmailEnUso(usuarioForm.getEmail())) {
            model.addAttribute("mensajeUsuario", MensajeUsuario.mensajeError(messageSource.getMessage("validation.login", null, locale)));
            return "gestion/usuarios/detalle";
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioForm.getNombre());
        usuario.setEmail(usuarioForm.getEmail());
        usuario.setDni(usuarioForm.getDni());
        usuario.setTelefono(usuarioForm.getTelefono());
        usuario.setRol(usuarioForm.getRol());

        // Contraseña inicial
        if (StringUtils.isNotEmpty(password) && StringUtils.isNotEmpty(confirmarPassword)) {
            if (password.equals(confirmarPassword)) {
                if (comprobarPoliticaCalidadPassword(password)) {
                    usuario.setPassword(passwordEncoder.encode(password));
                } else {
                    model.addAttribute("roles", Rol.values());
                    model.addAttribute("mensajeUsuario", MensajeUsuario.mensajeError(messageSource.getMessage("validation.password.no-calidad", null, locale )));
                    return "gestion/usuarios/detalle";
                }
            } else {
                //Las claves no son iguales
                model.addAttribute("roles", Rol.values());
                model.addAttribute("mensajeUsuario", MensajeUsuario.mensajeError(messageSource.getMessage("validation.password.no-iguales", null, locale)));
                return "gestion/usuarios/detalle";
            }
        } else {
            //La contraseña es obligatoria
            model.addAttribute("roles", Rol.values());
            model.addAttribute("mensajeUsuario", MensajeUsuario.mensajeError(messageSource.getMessage("validation.password.no-iguales", null, locale)));
            return "gestion/usuarios/detalle";
        }

        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute("mensajeUsuario", MensajeUsuario.mensajeCorrecto(messageSource.getMessage("formulario.guardado", null, locale)));

        return "redirect:/gestion/usuarios/detalle?id=" + usuario.getId();

    }

    @PostMapping("/editar")
    public String editar(@RequestParam("password") String password, @RequestParam("confirmarPassword") String confirmarPassword,
            @Valid @ModelAttribute("usuario") UsuarioForm usuarioForm, BindingResult result, Model model, Locale locale,
            RedirectAttributes redirectAttributes) throws Exception {

        if (result.hasErrors()) {
            model.addAttribute("roles", Rol.values());
            return "gestion/usuarios/detalle";
        }

        Usuario usuario = usuarioRepository.findById(usuarioForm.getId()).get();
        BeanUtils.copyProperties(usuarioForm, usuario);
        
        // Gestión del cambio de contraseña
        if (StringUtils.isNotEmpty(password)) {
            if (password.equals(confirmarPassword)) {
                if (comprobarPoliticaCalidadPassword(password)) {
                    usuario.setPassword(passwordEncoder.encode(password));
                } else {
                    model.addAttribute("mensajeUsuario", MensajeUsuario.mensajeError(messageSource.getMessage("validation.password.no-calidad", null, locale )));
                    model.addAttribute("roles", Rol.values());
                    return "gestion/usuarios/detalle";
                }
            } else {
                //Las claves no son iguales
                model.addAttribute("mensajeUsuario", MensajeUsuario.mensajeError(messageSource.getMessage("validation.password.no-iguales", null, locale)));
                model.addAttribute("roles", Rol.values());
                return "gestion/usuarios/detalle";
            }
        } 

        usuarioRepository.save(usuario);

        redirectAttributes.addFlashAttribute("mensajeUsuario", MensajeUsuario.mensajeCorrecto(messageSource.getMessage("formulario.guardado",null, locale)));
        
        return "redirect:/gestion/usuarios/detalle?id=" + usuario.getId();
    }


    @GetMapping("/eliminar")
    public String eliminar(@RequestParam("id") Long id, Model model) {
        Usuario usuario = usuarioRepository.findById(id).get();
        usuarioRepository.delete(usuario);
        return "redirect:lista";
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
    
}
