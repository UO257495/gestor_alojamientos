package com.nayarasanchez.gestor_alojamientos.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
import com.nayarasanchez.gestor_alojamientos.model.Reserva;
import com.nayarasanchez.gestor_alojamientos.service.AlojamientoService;
import com.nayarasanchez.gestor_alojamientos.service.ReservaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.temporal.ChronoUnit;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/reservar")
@RequiredArgsConstructor
public class ReservaController {

    private final AlojamientoService alojamientoService;
    private final ReservaService reservaService;
    //private final EmailService emailService; // TODO:

    @GetMapping("/{id}")
    public String verReserva(@PathVariable Long id,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
                             Model model) {

        Alojamiento alojamiento = alojamientoService.buscarPorId(id).get();
        double precioTotal = reservaService.calcularTotal(alojamiento.getId(), fechaInicio, fechaFin);

        model.addAttribute("alojamiento", alojamiento);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        model.addAttribute("precioTotal", precioTotal);

        return "reserva";
    }

    // @PostMapping("/pagar")
    // public String procesarPago(@RequestParam Long alojamientoId,
    //                            @RequestParam LocalDate fechaInicio,
    //                            @RequestParam LocalDate fechaFin,
    //                            @RequestParam double precioTotal,
    //                            @RequestParam String formaPago,
    //                            RedirectAttributes redirectAttrs) {

    //     // Crear reserva en estado PENDIENTE
    //     Reserva reserva = reservaService.crearReserva(alojamientoId, fechaInicio, fechaFin, precioTotal, formaPago);

    //     // Enviar email de confirmación
    //     //emailService.enviarConfirmacion(reserva);

    //     // Redirigir según forma de pago
    //     if ("paypal".equals(formaPago)) {
    //         return "redirect:/pago/paypal/" + reserva.getId();
    //     } else {
    //         redirectAttrs.addFlashAttribute("mensaje", "Reserva creada correctamente. Se le contactará para confirmar el pago.");
    //         return "redirect:/mis-reservas";
    //     }
    // }
}

