package com.nayarasanchez.gestor_alojamientos.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nayarasanchez.gestor_alojamientos.dto.view.MensajeUsuario;
import com.nayarasanchez.gestor_alojamientos.model.Reserva;
import com.nayarasanchez.gestor_alojamientos.model.Usuario;
import com.nayarasanchez.gestor_alojamientos.model.Valoracion;
import com.nayarasanchez.gestor_alojamientos.service.ReservaService;
import com.nayarasanchez.gestor_alojamientos.service.UsuarioService;
import com.nayarasanchez.gestor_alojamientos.service.ValoracionService;

@Controller
@RequestMapping("/valoraciones")
public class ValoracionController {

    @Autowired
    private ValoracionService valoracionService;

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/nueva/{reservaId}")
    public String mostrarFormularioValoracion(@PathVariable Long reservaId, Model model) {
        Optional<Reserva> reservaOpt = reservaService.buscarPorId(reservaId);

        if (reservaOpt.isEmpty()) {
            return "redirect:/gestion/reservas/lista";
        }

        Reserva reserva = reservaOpt.get();

        Valoracion valoracion = new Valoracion();
        valoracion.setReserva(reserva);
        valoracion.setFecha(LocalDate.now());

        model.addAttribute("reserva", reserva);
        model.addAttribute("valoracion", valoracion);
        model.addAttribute("reservaId", reserva.getId());
        model.addAttribute("clienteId", reserva.getCliente().getId());

        return "valoracion";
    }

    @PostMapping("/guardar")
    public String guardarValoracion(
            @RequestParam Long reservaId,
            @RequestParam Long clienteId,
            @RequestParam int puntuacion,
            @RequestParam String comentario,
            RedirectAttributes redirectAttrs) {

        Reserva reserva = reservaService.buscarPorId(reservaId).get();
        Usuario cliente = usuarioService.obtenerUsuarioPorId(clienteId).get();

        if (reserva == null || clienteId == null) {
            redirectAttrs.addFlashAttribute("mensajeUsuario", "Error al guardar la valoración.");
            return "redirect:/gestion/reservas/lista";
        }

        valoracionService.crearValoracion(reserva, cliente, puntuacion, comentario);

        redirectAttrs.addFlashAttribute("mensajeUsuario", MensajeUsuario.mensajeCorrecto("¡Gracias por dejar tu valoración!"));
        return "redirect:/gestion/reservas/lista";
    }

    @GetMapping("/lista")
    public String listarValoraciones(Model model) {

        var valoraciones = valoracionService.obtenerTodasOrdenadasPorFecha();

        model.addAttribute("valoraciones", valoraciones);

        return "gestion/valoraciones/lista"; 
    }

}
