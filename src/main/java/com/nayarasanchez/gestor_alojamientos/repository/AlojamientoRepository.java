package com.nayarasanchez.gestor_alojamientos.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;

public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long>{

    List<Alojamiento> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT a FROM Alojamiento a " +
        "WHERE (:direccion IS NULL OR LOWER(a.direccion) LIKE LOWER(CONCAT('%', :direccion, '%'))) " +
        "AND (:personas IS NULL OR a.capacidad >= :personas) " +  // Capacidad >= personas
        "AND (:fechaInicio IS NULL OR :fechaFin IS NULL OR NOT EXISTS (" +
        "     SELECT r FROM Reserva r " +
        "     WHERE r.alojamiento = a " +
        "       AND r.estado IN ('CONFIRMADA', 'PENDIENTE') " + // Solo reservas que bloquean el alojamiento
        "       AND r.fechaInicio <= :fechaFin " +
        "       AND r.fechaFin >= :fechaInicio" +
        "))")
    List<Alojamiento> buscarAlojamientosPorFiltro(
            @Param("direccion") String direccion,
            @Param("personas") Integer personas,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );


    
}
