package com.nayarasanchez.gestor_alojamientos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;

public interface AlojamientoRepository extends JpaRepository<Alojamiento, Long>{
    
}
