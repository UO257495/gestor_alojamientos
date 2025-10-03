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

import com.nayarasanchez.gestor_alojamientos.dto.form.TemporadaForm;
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
                        BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("alojamientos", alojamientoService.listarTodos());
            return "gestion/temporadas/detalle";
        }
        
        Temporada temporada = temporadaService.crearOActualizar(temporadaForm);
        
        return "redirect:/gestion/temporadas/detalle?id=" + temporada.getId();
    
    }

    @GetMapping("/eliminar")
    public String eliminar(@RequestParam("id") Long id) {
        temporadaService.eliminar(id);
        return "redirect:/gestion/temporadas/lista";
    }

}
