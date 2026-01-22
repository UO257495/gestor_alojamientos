package com.nayarasanchez.gestor_alojamientos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.nayarasanchez.gestor_alojamientos.dto.form.BusquedaAlojamientoForm;
import com.nayarasanchez.gestor_alojamientos.service.BusquedaAlojamientosService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BuscadorController {
    
    private final BusquedaAlojamientosService busquedaAlojamientosService;

    @GetMapping("/buscador")
	public String buscador(Model model) {
		model.addAttribute("busquedaForm", new BusquedaAlojamientoForm());
		model.addAttribute("resultados", busquedaAlojamientosService.listarTodos());
    	return "/buscador";
	}
}
