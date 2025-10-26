package com.nayarasanchez.gestor_alojamientos.controller.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nayarasanchez.gestor_alojamientos.dto.form.BusquedaAlojamientoForm;
import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
import com.nayarasanchez.gestor_alojamientos.service.BusquedaAlojamientosService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/alojamientos")
@RequiredArgsConstructor
public class BusquedaAlojamientosRestController {

    private final BusquedaAlojamientosService busquedaService;

    @GetMapping
    public List<Alojamiento> listarTodos() {
        return busquedaService.listarTodos();
    }

    @PostMapping("/buscar")
    public List<Alojamiento> buscar(@RequestBody BusquedaAlojamientoForm form) {
        return busquedaService.buscarPorFiltro(form);
    }
}
