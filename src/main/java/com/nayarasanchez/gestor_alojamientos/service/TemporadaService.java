package com.nayarasanchez.gestor_alojamientos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nayarasanchez.gestor_alojamientos.dto.form.TemporadaForm;
import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
import com.nayarasanchez.gestor_alojamientos.model.Temporada;
import com.nayarasanchez.gestor_alojamientos.repository.AlojamientoRepository;
import com.nayarasanchez.gestor_alojamientos.repository.TemporadaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemporadaService {
    
    private final TemporadaRepository temporadaRepository;
    private final AlojamientoRepository alojamientoRepository;

    public List<Temporada> listarTodas() {
        return temporadaRepository.findAll();
    }

    public Optional<Temporada> buscarPorId(Long id) {
        return temporadaRepository.findById(id);
    }

    public Temporada crearOActualizar(TemporadaForm form) {
        Temporada temporada;

        if (form.getId() != null) {
            temporada = temporadaRepository.findById(form.getId())
                    .orElse(new Temporada());
        } else {
            temporada = new Temporada();
        }

        Alojamiento alojamiento = alojamientoRepository.findById(form.getAlojamientoId())
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        boolean existeSolapamiento = temporadaRepository
                .existsByAlojamientoIdAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                        alojamiento.getId(), form.getFechaFin(), form.getFechaInicio());

        if (existeSolapamiento && (temporada.getId() == null)) {
            throw new IllegalArgumentException("Ya existe una temporada que se solapa en esas fechas");
        }

        temporada.setNombre(form.getNombre());
        temporada.setFechaInicio(form.getFechaInicio());
        temporada.setFechaFin(form.getFechaFin());
        temporada.setPrecio(form.getPrecio());
        temporada.setAlojamiento(alojamiento);

        return temporadaRepository.save(temporada);
    }

    public void eliminar(Long id) {
        temporadaRepository.deleteById(id);
    }

    
}
