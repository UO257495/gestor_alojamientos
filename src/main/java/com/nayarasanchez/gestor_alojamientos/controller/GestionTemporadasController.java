package com.nayarasanchez.gestor_alojamientos.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nayarasanchez.gestor_alojamientos.dto.form.TemporadaForm;
import com.nayarasanchez.gestor_alojamientos.dto.view.MensajeUsuario;
import com.nayarasanchez.gestor_alojamientos.model.Temporada;
import com.nayarasanchez.gestor_alojamientos.service.AlojamientoService;
import com.nayarasanchez.gestor_alojamientos.service.TemporadaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/gestion/temporadas")
public class GestionTemporadasController {
    
    private final TemporadaService temporadaService;
    private final AlojamientoService alojamientoService;
    

    @GetMapping("/lista")
    public String lista(Model model) {
        model.addAttribute("temporadas", temporadaService.listarTodas());
        return "gestion/temporadas/lista";
    }

    @GetMapping("/detalle")
    public String detalle(@RequestParam("id") Optional<Long> id, Model model) {
        Temporada temporada = id
                .flatMap(temporadaService::buscarPorId)
                .orElse(new Temporada());

        model.addAttribute("temporada", temporada);
        model.addAttribute("alojamientos", alojamientoService.listarTodos());
        return "gestion/temporadas/detalle";
    }
    
    @PostMapping("/nuevo")
    public String nuevo(@Valid @ModelAttribute("temporada") TemporadaForm temporadaForm,
                        BindingResult result, Model model, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("alojamientos", alojamientoService.listarTodos());
            return "gestion/temporadas/detalle";
        }
        
         try {
            temporadaService.crearOActualizar(temporadaForm);
            redirectAttributes.addFlashAttribute("mensajeUsuario", MensajeUsuario.mensajeCorrecto("Temporada guardada correctamente"));
            return "redirect:/gestion/temporadas/lista";
         } catch (Exception e) {
            model.addAttribute("mensajeUsuario", "Error guardando el alojamiento");
            return "gestion/alojamientos/detalle";
        }
    }

    @GetMapping("/eliminar")
    public String eliminar(@RequestParam("id") Long id) {
        temporadaService.eliminar(id);
        return "redirect:/gestion/temporadas/lista";
    }

}
