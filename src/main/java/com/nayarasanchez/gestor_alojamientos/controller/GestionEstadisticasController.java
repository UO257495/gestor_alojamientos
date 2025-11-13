package com.nayarasanchez.gestor_alojamientos.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
import com.nayarasanchez.gestor_alojamientos.model.Reserva;
import com.nayarasanchez.gestor_alojamientos.service.AlojamientoService;
import com.nayarasanchez.gestor_alojamientos.service.ReservaService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/gestion/estadisticas")
public class GestionEstadisticasController {
    
    private final ReservaService reservaService;
    private final AlojamientoService alojamientoService;

    @GetMapping("/estadisticas")
    public String verEstadisticas(@RequestParam(required = false) Integer anio, Model model) {
        int year = (anio != null) ? anio : LocalDate.now().getYear();

        List<Reserva> reservas = reservaService.obtenerReservasPorAnio(year);
        List<Alojamiento> alojamientos = alojamientoService.listarTodos();

        //Cálculos 
        Map<String, Long> reservasPorEstado = reservas.stream()
            .collect(Collectors.groupingBy(r -> r.getEstado().name(), Collectors.counting()));

        Map<String, Long> reservasPorPago = reservas.stream()
            .collect(Collectors.groupingBy(r -> r.getFormaPago().name(), Collectors.counting()));

        Map<Integer, Long> reservasPorMes = reservas.stream()
            .collect(Collectors.groupingBy(r -> r.getFechaInicio().getMonthValue(), Collectors.counting()));

        Map<Integer, Double> ingresosPorMes = reservas.stream()
            .filter(r -> r.getPrecioTotal() != null)
            .collect(Collectors.groupingBy(r -> r.getFechaInicio().getMonthValue(),
                    Collectors.summingDouble(Reserva::getPrecioTotal)));

        // Simulación de ocupación mensual 
        Map<Integer, Double> ocupacionPorMes = reservaService.calcularOcupacionMensual(year, reservas, alojamientos.size());

        // Formateo para ECharts 
        List<String> meses = List.of("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic");

        model.addAttribute("anioSeleccionado", year);
        model.addAttribute("aniosDisponibles", reservaService.obtenerAniosConDatos());

        model.addAttribute("estadisticasEstados", reservasPorEstado.entrySet().stream()
            .map(e -> Map.of("name", e.getKey(), "value", e.getValue()))
            .toList());

        model.addAttribute("estadisticasPagos", reservasPorPago.entrySet().stream()
            .map(e -> Map.of("name", e.getKey(), "value", e.getValue()))
            .toList());

        model.addAttribute("totalReservasMes", Map.of(
            "labels", meses,
            "values", IntStream.rangeClosed(1, 12)
                .mapToObj(m -> reservasPorMes.getOrDefault(m, 0L))
                .toList()
        ));

        model.addAttribute("ingresosMensuales", Map.of(
            "labels", meses,
            "values", IntStream.rangeClosed(1, 12)
                .mapToObj(m -> ingresosPorMes.getOrDefault(m, 0.0))
                .toList()
        ));

        model.addAttribute("ocupacionMensual", Map.of(
            "labels", meses,
            "values", IntStream.rangeClosed(1, 12)
                .mapToObj(m -> ocupacionPorMes.getOrDefault(m, 0.0))
                .toList()
        ));

        return "gestion/estadisticas/estadisticas";
    }


}
