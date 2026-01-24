package com.nayarasanchez.gestor_alojamientos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPermisosController {

    @GetMapping("/error/403")
    public String error403() {
        return "error/403";
    }
}
