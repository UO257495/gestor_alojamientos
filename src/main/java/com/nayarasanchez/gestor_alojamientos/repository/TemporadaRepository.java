package com.nayarasanchez.gestor_alojamientos.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nayarasanchez.gestor_alojamientos.model.Temporada;

public interface TemporadaRepository extends JpaRepository<Temporada, Long> {

    // Verifica si existe solapamiento de fechas en un alojamiento
    boolean existsByAlojamientoIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            Long alojamientoId, LocalDate fechaFin, LocalDate fechaInicio);

    /**
     * Devuelve el precio de la temporada que aplica a un alojamiento
     * durante el rango de fechas indicado.
     */
    @Query("""
        SELECT t.precio
        FROM Temporada t
        WHERE t.alojamiento.id = :alojamientoId
          AND (
                (:inicio BETWEEN t.fechaInicio AND t.fechaFin)
             OR (:fin BETWEEN t.fechaInicio AND t.fechaFin)
             OR (t.fechaInicio <= :inicio AND t.fechaFin >= :fin)
          )
        """)
    Optional<Double> findPrecioPorFechasYAlojamiento(
        @Param("inicio") LocalDate inicio,
        @Param("fin") LocalDate fin,
        @Param("alojamientoId") Long alojamientoId
    );

    
}

