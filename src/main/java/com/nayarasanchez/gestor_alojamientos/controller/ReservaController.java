package com.nayarasanchez.gestor_alojamientos.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nayarasanchez.gestor_alojamientos.dto.form.ReservaForm;
import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
import com.nayarasanchez.gestor_alojamientos.model.EstadoReserva;
import com.nayarasanchez.gestor_alojamientos.model.Reserva;
import com.nayarasanchez.gestor_alojamientos.model.Usuario;
import com.nayarasanchez.gestor_alojamientos.service.AlojamientoService;
import com.nayarasanchez.gestor_alojamientos.service.ReservaService;
import com.nayarasanchez.gestor_alojamientos.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/reservar")
@RequiredArgsConstructor
public class ReservaController {

    private final AlojamientoService alojamientoService;
    private final ReservaService reservaService;
    private final UsuarioService usuarioService;
    //private final EmailService emailService; // TODO:


    @GetMapping("/{id}")
    public String verReserva(@PathVariable Long id,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                            Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
         Usuario usuarioActual = usuarioService.obtenerUsuarioPorEmail(username)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Alojamiento alojamiento = alojamientoService.buscarPorId(id).orElseThrow();

        if (fechaInicio == null) fechaInicio = LocalDate.now();
        if (fechaFin == null) fechaFin = fechaInicio.plusDays(1);

        double precioTotal = reservaService.calcularTotal(alojamiento.getId(), fechaInicio, fechaFin);

        ReservaForm form = new ReservaForm();
        form.setAlojamientoId(alojamiento.getId());
        form.setClienteId(usuarioActual.getId());
        form.setFechaInicio(fechaInicio);
        form.setFechaFin(fechaFin);
        form.setPrecioTotal(precioTotal);

        model.addAttribute("reservaForm", form);
        model.addAttribute("alojamiento", alojamiento);
        model.addAttribute("clienteNombre", usuarioActual.getNombre());

        return "/reserva";
    }


    @PostMapping("/confirmar")
    public String confirmarReserva(@ModelAttribute("reservaForm") ReservaForm reservaForm, Model model) {

        reservaService.crearReservaCliente(reservaForm);

        // Puedes añadir un mensaje o redirigir
        model.addAttribute("mensaje", "Reserva confirmada correctamente");
        return "redirect:/gestion/reservas/lista";
    }

}

