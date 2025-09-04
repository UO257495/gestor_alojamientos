package com.nayarasanchez.gestor_alojamientos.controller;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.nayarasanchez.gestor_alojamientos.dto.form.AlojamientoForm;
import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
import com.nayarasanchez.gestor_alojamientos.repository.AlojamientoRepository;
import com.nayarasanchez.gestor_alojamientos.service.S3Service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/gestion/alojamientos")
public class AlojamientosController {
    
    @Autowired
    private S3Service s3Service;

    private final AlojamientoRepository alojamientoRepository;

    @GetMapping("/lista")
    public String lista(Model model) {
        model.addAttribute("alojamientos", alojamientoRepository.findAll());
        return "gestion/alojamientos/lista";
    }

    @GetMapping("/detalle")
    public String detalle(@RequestParam("id") Optional<Long> id, Model model) {
        
        Alojamiento alojamiento = new Alojamiento();
        if (id.isPresent()) {
            Optional<Alojamiento> alojamientoOpt = alojamientoRepository.findById(id.get());
            alojamiento = alojamientoOpt.orElse(new Alojamiento());
        }

        model.addAttribute("alojamiento", alojamiento);

        return "gestion/alojamientos/detalle";
    }

    @PostMapping("/nuevo")
    public String nuevo(@Valid @ModelAttribute("alojamiento") AlojamientoForm alojamientoForm,
                        BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "gestion/alojamientos/detalle";
        }

        Alojamiento alojamiento = new Alojamiento();
        alojamiento.setNombre(alojamientoForm.getNombre());
        alojamiento.setDescripcion(alojamientoForm.getDescripcion());
        alojamiento.setDireccion(alojamientoForm.getDireccion());
        alojamiento.setLatitud(alojamientoForm.getLatitud());
        alojamiento.setLongitud(alojamientoForm.getLongitud());
        alojamiento.setPropietario(null);

        // Subir foto a S3
        if (alojamientoForm.getFoto() != null && !alojamientoForm.getFoto().isEmpty()) {
            try {
                String url = s3Service.uploadFile(alojamientoForm.getFoto());
                alojamiento.setFoto(url);
            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("mensajeUsuario", "Error subiendo la foto");
                return "gestion/alojamientos/detalle";
            }
        }

        alojamientoRepository.save(alojamiento);

        return "redirect:/gestion/alojamientos/detalle?id=" + alojamiento.getId();
    }

    
}
