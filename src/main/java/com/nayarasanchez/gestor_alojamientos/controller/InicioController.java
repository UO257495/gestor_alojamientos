package com.nayarasanchez.gestor_alojamientos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class InicioController {

    @GetMapping("/inicio")
	public String inicio(Model model) {
    	return "/inicio";
	}
}
