package com.nayarasanchez.gestor_alojamientos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;

public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long>{

    List<Alojamiento> findByNombreContainingIgnoreCase(String nombre);
    
    @Query("SELECT a.precioBase FROM Alojamiento a WHERE a.id = :id")
    Optional<Double> findPrecioBaseById(@Param("id") Long id);
}
