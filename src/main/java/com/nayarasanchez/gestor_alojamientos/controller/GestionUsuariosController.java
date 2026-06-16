package com.nayarasanchez.gestor_alojamientos.controller;

import java.util.Locale;
import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
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
import com.nayarasanchez.gestor_alojamientos.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/gestion/usuarios")
@RequiredArgsConstructor
@Slf4j
public class GestionUsuariosController {
    
    private final UsuarioService usuarioService;
    
    private final MessageSource messageSource;

    @GetMapping("/lista")
    public String lista(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        return "gestion/usuarios/lista";
    }

    @GetMapping("/detalle")
    public String detalle(@RequestParam("id") Optional<Long> id, Model model) {

        Usuario usuario = id.flatMap(usuarioService::obtenerUsuarioPorId).orElse(new Usuario());
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", Rol.values());
        model.addAttribute("soloLecturaRoles", false);
        model.addAttribute("modoPerfil", false);
        model.addAttribute("esRegistroPublico", false);

        return "gestion/usuarios/detalle";
    }


    @GetMapping("/nuevo")
    public String mostrarFormularioRegistro(Model model, Authentication auth) {
        Usuario usuario = new Usuario();
        
        boolean esRegistroPublico =
            auth == null
            || !auth.isAuthenticated()
            || auth.getName().equals("anonymousUser");

        if (esRegistroPublico) {
            usuario.setRol(Rol.CLIENTE);
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", Rol.values());
        model.addAttribute("soloLecturaRoles", false); 
        model.addAttribute("modoPerfil", false);
        model.addAttribute("esRegistroPublico", esRegistroPublico);

        return "gestion/usuarios/detalle";
    }

    @PostMapping("/nuevo")
    public String nuevo(@RequestParam("password") String password,
                        @RequestParam("confirmarPassword") String confirmarPassword,
                        @Valid @ModelAttribute("usuario") UsuarioForm usuarioForm,
                        BindingResult result, Model model, Locale locale,
                        RedirectAttributes redirectAttributes, Authentication auth) {

        boolean esRegistroPublico =
        auth == null
        || !auth.isAuthenticated()
        || auth.getName().equals("anonymousUser");

        if (esRegistroPublico &&
            (usuarioForm.getAceptaPrivacidad() == null || !usuarioForm.getAceptaPrivacidad())) {

            result.rejectValue(
                "aceptaPrivacidad",
                "error.usuario",
                "Debe aceptar la política de privacidad"
            );

            model.addAttribute("mensajePrivacidad", "Debe aceptar la política de privacidad");
        }

        if (usuarioService.comprobarDniEnUso(usuarioForm.getDni())) {
            result.rejectValue("dni", "error.usuario", "Ya existe un usuario registrado con este DNI.");
        }

        if (result.hasErrors()) {
            model.addAttribute("roles", Rol.values());
            model.addAttribute("soloLecturaRoles", false);
            model.addAttribute("modoPerfil", false);
            model.addAttribute("esRegistroPublico", esRegistroPublico);

            model.addAttribute("mensajeUsuario",
                MensajeUsuario.mensajeError("Revisa los campos del formulario"));

            return "gestion/usuarios/detalle";
        }

        if (!password.equals(confirmarPassword)) {
            model.addAttribute("roles", Rol.values());
            model.addAttribute("soloLecturaRoles", false);
            model.addAttribute("modoPerfil", false);
            model.addAttribute("esRegistroPublico", esRegistroPublico);

            model.addAttribute("mensajeUsuario",
                MensajeUsuario.mensajeError(
                    messageSource.getMessage("validation.password.no-iguales", null, locale)));

            return "gestion/usuarios/detalle";
        }

        if (usuarioService.comprobarEmailEnUso(usuarioForm.getEmail())) {
            model.addAttribute("roles", Rol.values());
            model.addAttribute("soloLecturaRoles", false);
            model.addAttribute("modoPerfil", false);
            model.addAttribute("esRegistroPublico", esRegistroPublico);

            model.addAttribute("mensajeUsuario", MensajeUsuario.mensajeError(
                    messageSource.getMessage("validation.login", null, locale)));

            return "gestion/usuarios/detalle";
        }

        if (!usuarioService.comprobarPoliticaCalidadPassword(password)) {
            model.addAttribute("roles", Rol.values());
            model.addAttribute("soloLecturaRoles", false);
            model.addAttribute("modoPerfil", false);
            model.addAttribute("esRegistroPublico", esRegistroPublico);

            model.addAttribute("mensajeUsuario", MensajeUsuario.mensajeError(
                    messageSource.getMessage("validation.password.no-calidad", null, locale)));

            return "gestion/usuarios/detalle";
        }

        usuarioService.crearUsuario(usuarioForm, password, auth);
        if(auth == null){
            redirectAttributes.addFlashAttribute("mensajeUsuario", MensajeUsuario.mensajeCorrecto(
                messageSource.getMessage("creacion.usuario.cliente", null, locale)));
                return "redirect:/login";
        } else{
            redirectAttributes.addFlashAttribute("mensajeUsuario", MensajeUsuario.mensajeCorrecto(
                    messageSource.getMessage("formulario.nuevo.usuario", null, locale)));
            
            return "redirect:/gestion/usuarios/lista";
        }
    }

    @PostMapping("/editar")
    public String editar(@RequestParam("password") String password,
                        @RequestParam("confirmarPassword") String confirmarPassword,
                        @Valid @ModelAttribute("usuario") UsuarioForm usuarioForm,
                        BindingResult result, Model model, Locale locale,
                        RedirectAttributes redirectAttributes,
                        Authentication auth) {

        boolean esAdmin = auth.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        if (!esAdmin) {
            return "redirect:/gestion/usuarios/perfil";
        }

        if (result.hasErrors()) {
            model.addAttribute("roles", Rol.values());
            return "gestion/usuarios/detalle";
        }

        if (usuarioService.comprobarDniEnUso(usuarioForm.getDni(), usuarioForm.getId())) {
            model.addAttribute("roles", Rol.values());
            model.addAttribute("mensajeUsuario", MensajeUsuario.mensajeError("Ese DNI ya está registrado en otro usuario."));
            return "gestion/usuarios/detalle";
        }
        
        if (!password.isBlank()) {
            if (!password.equals(confirmarPassword)) {
                model.addAttribute("roles", Rol.values());
                model.addAttribute("mensajeUsuario",
                    MensajeUsuario.mensajeError(messageSource.getMessage("validation.password.no-iguales", null, locale)));
                return "gestion/usuarios/detalle";
            }

            if (!usuarioService.comprobarPoliticaCalidadPassword(password)) {
                model.addAttribute("roles", Rol.values());
                model.addAttribute("mensajeUsuario",
                    MensajeUsuario.mensajeError(messageSource.getMessage("validation.password.no-calidad", null, locale)));
                return "gestion/usuarios/detalle";
            }
        }

        Usuario usuarioExistente = usuarioService.obtenerUsuarioPorId(usuarioForm.getId()).orElseThrow();
        usuarioService.editarUsuario(usuarioExistente, usuarioForm, password); 

        redirectAttributes.addFlashAttribute("mensajeUsuario",
            MensajeUsuario.mensajeCorrecto(messageSource.getMessage("formulario.usuario.modificado", null, locale)));

       return "redirect:/gestion/usuarios/lista";
    }

    @GetMapping("/eliminar")
    public String eliminar(@RequestParam("id") Long id) {
        usuarioService.eliminarUsuario(id);
        return "redirect:lista";
    }

    @GetMapping("/perfil")
    public String perfil(Model model, Authentication auth) {

        String email = auth.getName();
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("usuario", usuario);
        model.addAttribute("soloLecturaRoles", true);
        model.addAttribute("modoPerfil", true);
        model.addAttribute("esRegistroPublico", false);

        return "gestion/usuarios/detalle";
    }

    @PostMapping("/perfil")
    public String guardarPerfil(@RequestParam("password") String password,
                                @RequestParam("confirmarPassword") String confirmarPassword,
                                @Valid @ModelAttribute("usuario") UsuarioForm usuarioForm,
                                BindingResult result, Model model, Locale locale,
                                RedirectAttributes redirectAttributes,
                                Authentication auth) {

        Usuario usuarioActual = usuarioService.obtenerUsuarioPorEmail(auth.getName())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (result.hasErrors()) {
            model.addAttribute("usuario", usuarioActual);
            model.addAttribute("soloLecturaRoles", true);
            model.addAttribute("modoPerfil", true);
            model.addAttribute("mensajeUsuario",
            MensajeUsuario.mensajeError("Revisa los campos del formulario"));
            return "gestion/usuarios/detalle";
        }

        if (!password.isBlank()) {
            if (!password.equals(confirmarPassword)) {
                model.addAttribute("usuario", usuarioActual);
                model.addAttribute("soloLecturaRoles", true);
                model.addAttribute("modoPerfil", true);
                model.addAttribute("mensajeUsuario",
                    MensajeUsuario.mensajeError(messageSource.getMessage("validation.password.no-iguales", null, locale)));
                return "gestion/usuarios/detalle";
            }

            if (!usuarioService.comprobarPoliticaCalidadPassword(password)) {
                model.addAttribute("usuario", usuarioActual);
                model.addAttribute("soloLecturaRoles", true);
                model.addAttribute("modoPerfil", true);
                model.addAttribute("mensajeUsuario",
                    MensajeUsuario.mensajeError(messageSource.getMessage("validation.password.no-calidad", null, locale)));
                return "gestion/usuarios/detalle";
            }
        }

        usuarioForm.setId(usuarioActual.getId());
        usuarioForm.setEmail(usuarioActual.getEmail());

        usuarioService.editarPerfilUsuario(usuarioActual, usuarioForm, password);

        redirectAttributes.addFlashAttribute("mensajeUsuario",
            MensajeUsuario.mensajeCorrecto(messageSource.getMessage("formulario.guardado", null, locale)));

        return "redirect:/gestion/usuarios/perfil";
    }

    

}
