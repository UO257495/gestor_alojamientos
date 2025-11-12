package com.nayarasanchez.gestor_alojamientos.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nayarasanchez.gestor_alojamientos.model.Reserva;
import com.nayarasanchez.gestor_alojamientos.model.Usuario;
import com.nayarasanchez.gestor_alojamientos.model.Valoracion;
import com.nayarasanchez.gestor_alojamientos.repository.ValoracionRepository;

@Service
public class ValoracionService {

    @Autowired
    private ValoracionRepository valoracionRepository;

     public void crearValoracion(Valoracion valoracion) {
        valoracionRepository.save(valoracion);
    }

    public void crearValoracion(Reserva reserva, Usuario cliente, int puntuacion, String comentario) {
        Valoracion valoracion = new Valoracion();
        valoracion.setReserva(reserva);
        valoracion.setCliente(cliente);
        valoracion.setPuntuacion(puntuacion);
        valoracion.setComentario(comentario);
        valoracion.setFecha(LocalDate.now());
        valoracionRepository.save(valoracion);
    }

    public boolean existeValoracionParaReserva(Reserva reserva) {
        return valoracionRepository.findByReserva(reserva).isPresent();
    }

    public List<Valoracion> obtenerTodasOrdenadasPorFecha() {
        return valoracionRepository.findAllByOrderByFechaDesc();
    }
}