package com.nayarasanchez.gestor_alojamientos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.nayarasanchez.gestor_alojamientos.dto.form.BusquedaAlojamientoForm;

@Controller
public class InicioController {
    
    @GetMapping("/inicio")
	public String inicio(Model model) {
		model.addAttribute("busquedaForm", new BusquedaAlojamientoForm());
    	return "/inicio";
	}
}
