package com.nayarasanchez.gestor_alojamientos.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nayarasanchez.gestor_alojamientos.dto.form.AlojamientoForm;
import com.nayarasanchez.gestor_alojamientos.dto.form.BusquedaAlojamientoForm;
import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
import com.nayarasanchez.gestor_alojamientos.service.AlojamientoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/gestion/alojamientos")
public class GestionAlojamientosController {
    
    private final AlojamientoService alojamientoService;

    @GetMapping("/lista")
    public String lista(Model model) {
        model.addAttribute("alojamientos", alojamientoService.listarTodos());
        return "gestion/alojamientos/lista";
    }

    @GetMapping("/detalle")
    public String detalle(@RequestParam("id") Optional<Long> id, Model model) {
        Alojamiento alojamiento = id
                .flatMap(alojamientoService::buscarPorId)
                .orElse(new Alojamiento());

        model.addAttribute("alojamiento", alojamiento);
        return "gestion/alojamientos/detalle";
    }

    @PostMapping("/nuevo")
    public String nuevo(@Valid @ModelAttribute("alojamiento") AlojamientoForm alojamientoForm,
                        BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "gestion/alojamientos/detalle";
        }

        try {
            Alojamiento alojamiento = alojamientoService.crearOActualizar(alojamientoForm);
            return "redirect:/gestion/alojamientos/detalle?id=" + alojamiento.getId();
        } catch (Exception e) {
            model.addAttribute("mensajeUsuario", "Error guardando el alojamiento");
            return "gestion/alojamientos/detalle";
        }
    }


    @GetMapping("/buscar")
    public String buscar(@ModelAttribute("busquedaForm") BusquedaAlojamientoForm form, Model model) {
        List<Alojamiento> alojamientos = alojamientoService.buscar(form);
        model.addAttribute("alojamientos", alojamientos);
        return "gestion/alojamientos/busqueda";
    }

    @GetMapping("/eliminar")
    public String eliminar(@RequestParam("id") Long id) {
        alojamientoService.eliminar(id);
        return "redirect:lista";
    }
        
}
