package com.nayarasanchez.gestor_alojamientos.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nayarasanchez.gestor_alojamientos.model.EstadoReserva;
import com.nayarasanchez.gestor_alojamientos.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    // Buscar reservas por cliente, alojamiento, fechas, etc., si lo necesitas:
    List<Reserva> findByAlojamientoId(Long alojamientoId);

    List<Reserva> findByClienteId(Long clienteId);

    List<Reserva> findByAlojamientoIdAndEstadoNotIn(Long alojamientoId, List<EstadoReserva> estados);

    @Query("SELECT DISTINCT YEAR(r.fechaInicio) FROM Reserva r ORDER BY YEAR(r.fechaInicio)")
    List<Integer> findDistinctAnios();

    List<Reserva> findByFechaInicioBetween(LocalDate inicio, LocalDate fin);
    
}
