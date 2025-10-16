package com.nayarasanchez.gestor_alojamientos.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nayarasanchez.gestor_alojamientos.dto.form.ReservaForm;
import com.nayarasanchez.gestor_alojamientos.dto.view.MensajeUsuario;
import com.nayarasanchez.gestor_alojamientos.model.Reserva;
import com.nayarasanchez.gestor_alojamientos.service.AlojamientoService;
import com.nayarasanchez.gestor_alojamientos.service.ReservaService;
import com.nayarasanchez.gestor_alojamientos.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/gestion/reservas")
public class GestionReservasController {
    
    private final ReservaService reservaService;
    private final AlojamientoService alojamientoService;
    private final UsuarioService usuarioService;
    

    @GetMapping("/lista")
    public String lista(Model model) {
        model.addAttribute("reservas", reservaService.listarTodas());
        return "gestion/reservas/lista";
    }

    @GetMapping("/detalle")
    public String detalle(@RequestParam("id") Optional<Long> id, Model model) {
        Reserva reserva = id
                .flatMap(reservaService::buscarPorId)
                .orElse(new Reserva());

        model.addAttribute("reserva", reserva);
        model.addAttribute("clientes", usuarioService.listarClientes());
        model.addAttribute("alojamientos", alojamientoService.listarTodos());
        return "gestion/reservas/detalle";
    }
    
    @PostMapping("/nuevo")
    public String nuevo(@Valid @ModelAttribute("reserva") ReservaForm reservaForm,
                        BindingResult result, Model model, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("clientes", usuarioService.listarClientes());
            model.addAttribute("alojamientos", alojamientoService.listarTodos());
            return "gestion/reservas/detalle";
        }
        
         try {
            reservaService.crearOActualizar(reservaForm);
            redirectAttributes.addFlashAttribute("mensajeUsuario", MensajeUsuario.mensajeCorrecto("Reserva creada correctamente"));
            return "redirect:/gestion/reservas/lista";
         } catch (Exception e) {
            model.addAttribute("mensajeUsuario", MensajeUsuario.mensajeError("Error creando la reserva"));
            return "gestion/reservas/detalle";
        }
    }

    @GetMapping("/eliminar")
    public String eliminar(@RequestParam("id") Long id) {
        reservaService.eliminar(id);
        return "redirect:/gestion/reservas/lista";
    }

    @GetMapping("/calcular-total")
    @ResponseBody
    public Double calcularTotal(@RequestParam Long alojamientoId,
                                @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate inicio,
                                @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fin) {
        return reservaService.calcularTotal(alojamientoId, inicio, fin);
    }

    @GetMapping("/gestion/alojamientos/{id}/fechas-ocupadas")
    @ResponseBody
    public List<Map<String, String>> obtenerFechasOcupadas(@PathVariable Long id) {
        return reservaService.findByAlojamientoId(id).stream()
            .map(r -> Map.of(
                "inicio", r.getFechaInicio().toString(),
                "fin", r.getFechaFin().toString()
            ))
            .toList();
    }

}
