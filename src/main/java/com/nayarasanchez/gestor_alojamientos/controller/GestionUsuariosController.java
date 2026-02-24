package com.nayarasanchez.gestor_alojamientos.controller;

import java.net.Authenticator;
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

        return "gestion/usuarios/detalle";
    }

    @PostMapping("/nuevo")
    public String nuevo(@RequestParam("password") String password,
                        @RequestParam("confirmarPassword") String confirmarPassword,
                        @Valid @ModelAttribute("usuario") UsuarioForm usuarioForm,
                        BindingResult result, Model model, Locale locale,
                        RedirectAttributes redirectAttributes, Authentication auth) {

        if (result.hasErrors() || !password.equals(confirmarPassword)) {
            model.addAttribute("roles", Rol.values());
            model.addAttribute("mensajeUsuario",
                MensajeUsuario.mensajeError(password.equals(confirmarPassword) 
                    ? messageSource.getMessage("validation.password.no-calidad", null, locale)
                    : messageSource.getMessage("validation.password.no-iguales", null, locale)));
            return "gestion/usuarios/detalle";
        }

        if (usuarioService.comprobarEmailEnUso(usuarioForm.getEmail())) {
            model.addAttribute("roles", Rol.values());
            model.addAttribute("mensajeUsuario", MensajeUsuario.mensajeError(
                    messageSource.getMessage("validation.login", null, locale)));
            return "gestion/usuarios/detalle";
        }

        if (!usuarioService.comprobarPoliticaCalidadPassword(password)) {
            model.addAttribute("roles", Rol.values());
            model.addAttribute("mensajeUsuario", MensajeUsuario.mensajeError(
                    messageSource.getMessage("validation.password.no-calidad", null, locale)));
            return "gestion/usuarios/detalle";
        }

        Usuario usuario = usuarioService.crearUsuario(usuarioForm, password, auth);
        if(auth == null){
            redirectAttributes.addFlashAttribute("mensajeUsuario", MensajeUsuario.mensajeCorrecto(
                messageSource.getMessage("creacion.usuario.cliente", null, locale)));
                return "redirect:/login";
        } else{
            redirectAttributes.addFlashAttribute("mensajeUsuario", MensajeUsuario.mensajeCorrecto(
                    messageSource.getMessage("formulario.guardado", null, locale)));
            
            return "redirect:/gestion/usuarios/detalle?id=" + usuario.getId();
        }
    }

    @PostMapping("/editar")
    public String editar(@RequestParam("password") String password,
                        @RequestParam("confirmarPassword") String confirmarPassword,
                        @Valid @ModelAttribute("usuario") UsuarioForm usuarioForm,
                        BindingResult result, Model model, Locale locale,
                        RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("roles", Rol.values());
            return "gestion/usuarios/detalle";
        }

        // password opcional
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
        usuarioService.editarUsuario(usuarioExistente, usuarioForm, password); // si password vacío, tu service debe ignorarla

        redirectAttributes.addFlashAttribute("mensajeUsuario",
            MensajeUsuario.mensajeCorrecto(messageSource.getMessage("formulario.guardado", null, locale)));

        return "redirect:/gestion/usuarios/detalle?id=" + usuarioExistente.getId();
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

        // password opcional
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

        usuarioService.editarUsuario(usuarioActual, usuarioForm, password);

        redirectAttributes.addFlashAttribute("mensajeUsuario",
            MensajeUsuario.mensajeCorrecto(messageSource.getMessage("formulario.guardado", null, locale)));

        return "redirect:/gestion/usuarios/perfil";
    }

}
