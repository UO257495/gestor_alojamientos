package com.nayarasanchez.gestor_alojamientos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    
    @GetMapping("/login")
	public String login() {
    	return "/auth/login";
	}
    
}
