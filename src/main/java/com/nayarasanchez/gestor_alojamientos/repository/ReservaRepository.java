package com.nayarasanchez.gestor_alojamientos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nayarasanchez.gestor_alojamientos.model.EstadoReserva;
import com.nayarasanchez.gestor_alojamientos.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    // Buscar reservas por cliente, alojamiento, fechas, etc., si lo necesitas:
    List<Reserva> findByAlojamientoId(Long alojamientoId);

    List<Reserva> findByClienteId(Long clienteId);

    List<Reserva> findByAlojamientoIdAndEstadoNotIn(Long alojamientoId, List<EstadoReserva> estados);

}
