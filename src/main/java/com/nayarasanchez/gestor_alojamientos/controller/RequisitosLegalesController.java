package com.nayarasanchez.gestor_alojamientos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RequisitosLegalesController {
    
    @GetMapping("/privacidad")
    public String privacidad() {
        return "legal/privacidad";
    }

    @GetMapping("/cookies")
    public String cookies() {
        return "legal/cookies";
    }

    @GetMapping("/aviso-legal")
    public String avisoLegal() {
        return "legal/aviso-legal";
    }

}
