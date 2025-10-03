package com.nayarasanchez.gestor_alojamientos.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nayarasanchez.gestor_alojamientos.model.Temporada;

public interface TemporadaRepository extends JpaRepository<Temporada, Long> {

    // Verifica si existe solapamiento de fechas en un alojamiento
    boolean existsByAlojamientoIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            Long alojamientoId, LocalDate fechaFin, LocalDate fechaInicio);
}

