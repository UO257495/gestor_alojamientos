package com.nayarasanchez.gestor_alojamientos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nayarasanchez.gestor_alojamientos.model.Reserva;
import com.nayarasanchez.gestor_alojamientos.model.Usuario;
import com.nayarasanchez.gestor_alojamientos.model.Valoracion;

public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {

    Optional<Valoracion> findByReserva(Reserva reserva);

    List<Valoracion> findByCliente(Usuario cliente);

    List<Valoracion> findAllByOrderByFechaDesc();
    
}