package com.nayarasanchez.gestor_alojamientos.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nayarasanchez.gestor_alojamientos.dto.form.ReservaForm;
import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
import com.nayarasanchez.gestor_alojamientos.model.EstadoReserva;
import com.nayarasanchez.gestor_alojamientos.model.Reserva;
import com.nayarasanchez.gestor_alojamientos.model.Usuario;
import com.nayarasanchez.gestor_alojamientos.repository.AlojamientoRepository;
import com.nayarasanchez.gestor_alojamientos.repository.ReservaRepository;
import com.nayarasanchez.gestor_alojamientos.repository.TemporadaRepository;
import com.nayarasanchez.gestor_alojamientos.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservaService {
    
    private final ReservaRepository reservaRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final TemporadaRepository temporadaRepository;
    private final UsuarioRepository usuarioRepository;
    
    /**
     * Lista todas las reservas registrados
     */
    public List<Reserva> listarTodas() {
        return reservaRepository.findAll();
    }

    /**
     * Busca un reserva por su ID
     */
    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepository.findById(id);
    }

     /**
     * Busca un reserva por email del cliente
     */
    public List<Reserva> buscarPorClienteId(Long id) {
        return reservaRepository.findByClienteId(id);
    }

    
    /**
     * Crea o actualiza una reserva
     */
    public Reserva crearOActualizar(ReservaForm form) {
        Reserva reserva = (form.getId() != null)
                ? reservaRepository.findById(form.getId()).orElse(new Reserva())
                : new Reserva();

        reserva.setFechaInicio(form.getFechaInicio());
        reserva.setFechaFin(form.getFechaFin());
        reserva.setEstado(form.getEstado());

        Usuario cliente = usuarioRepository.findById(form.getClienteId()).orElseThrow();
        reserva.setCliente(cliente);

        Alojamiento alojamiento = alojamientoRepository.findById(form.getAlojamientoId()).orElseThrow();
        reserva.setAlojamiento(alojamiento);

        reserva.setPrecioTotal(form.getPrecioTotal());
        reserva.setFormaPago(form.getFormaPago());

        return reservaRepository.save(reserva);
    }

        /**
     * Crea o actualiza una reserva nueva de un cliente
     */
    public Reserva crearReservaCliente(ReservaForm form) {
        Reserva reserva = (form.getId() != null)
                ? reservaRepository.findById(form.getId()).orElse(new Reserva())
                : new Reserva();

        reserva.setFechaInicio(form.getFechaInicio());
        reserva.setFechaFin(form.getFechaFin());
        reserva.setEstado(EstadoReserva.PENDIENTE);

        Usuario cliente = usuarioRepository.findById(form.getClienteId()).orElseThrow();
        reserva.setCliente(cliente);

        Alojamiento alojamiento = alojamientoRepository.findById(form.getAlojamientoId()).orElseThrow();
        reserva.setAlojamiento(alojamiento);

        reserva.setPrecioTotal(form.getPrecioTotal());
        reserva.setFormaPago(form.getFormaPago());

        return reservaRepository.save(reserva);
    }

    public void eliminar(Long id) {
        reservaRepository.deleteById(id);
    }

    /**
     * Calcula el precio total de una reserva según las fechas y el alojamiento.
     */
    public double calcularTotal(Long alojamientoId, LocalDate inicio, LocalDate fin) {
        if (alojamientoId == null || inicio == null || fin == null || fin.isBefore(inicio)) {
            return 0.0;
        }

        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        double precioBase = alojamiento.getTarifaBase(); 

        // Buscar la temporada activa en esas fechas
        Double precioTemporada = temporadaRepository
                .findPrecioPorFechasYAlojamiento(inicio, fin, alojamientoId)
                .orElse(0.0);

        long dias = ChronoUnit.DAYS.between(inicio, fin);
        if (dias <= 0) dias = 1; // al menos 1 día

        return (precioBase + precioTemporada) * dias;
    }

    public List<Reserva> findFechasOcupadas(Long alojamientoId) {
        return reservaRepository.findByAlojamientoIdAndEstadoNotIn(
            alojamientoId,
            List.of(EstadoReserva.CANCELADA, EstadoReserva.RECHAZADA)
        );
    }

    public Collection<Reserva> findByAlojamientoId(Long id) {
        return reservaRepository.findByAlojamientoId(id);
    }

}

