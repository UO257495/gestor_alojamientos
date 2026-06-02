package com.nayarasanchez.gestor_alojamientos.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nayarasanchez.gestor_alojamientos.dto.form.AlojamientoForm;
import com.nayarasanchez.gestor_alojamientos.dto.form.BusquedaAlojamientoForm;
import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
import com.nayarasanchez.gestor_alojamientos.repository.AlojamientoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlojamientoService {

    private final AlojamientoRepository alojamientoRepository;

    @Autowired
    private SupabaseStorageService storageService;

 
    public List<Alojamiento> listarTodos() {
        return alojamientoRepository.findAll();
    }

    public Optional<Alojamiento> buscarPorId(Long id) {
        return alojamientoRepository.findById(id);
    }

    public Alojamiento crearOActualizar(AlojamientoForm form) throws IOException {
        Alojamiento alojamiento;

        if (form.getId() != null) {
            alojamiento = alojamientoRepository.findById(form.getId())
                    .orElse(new Alojamiento());
        } else {
            alojamiento = new Alojamiento();
        }

        alojamiento.setNombre(form.getNombre());
        alojamiento.setDescripcion(form.getDescripcion());
        alojamiento.setDireccion(form.getDireccion());
        alojamiento.setLatitud(form.getLatitud());
        alojamiento.setLongitud(form.getLongitud());
        alojamiento.setTarifaBase(form.getTarifaBase());
        alojamiento.setPropietario(null);
        alojamiento.setCapacidad(form.getCapacidad());

        MultipartFile foto = form.getFoto();
        if (foto != null && !foto.isEmpty()) {
            String url = storageService.uploadFile(foto);
            alojamiento.setFoto(url);
        }


        return alojamientoRepository.save(alojamiento);
    }

    public List<Alojamiento> buscar(BusquedaAlojamientoForm form) {
        if (form.getNombre() != null && !form.getNombre().isBlank()) {
            return alojamientoRepository.findByNombreContainingIgnoreCase(form.getNombre());
        }
        return alojamientoRepository.findAll();
    }

    public boolean eliminar(Long id) {
        if (alojamientoRepository.tieneReservas(id)) {
            return false;
        }

        alojamientoRepository.findById(id).ifPresent(alojamiento -> {
            if (alojamiento.getFoto() != null && !alojamiento.getFoto().isBlank()) {
                try {
                    storageService.deleteFile(alojamiento.getFoto());
                } catch (Exception e) {
                    log.error("Error al eliminar la foto: " + alojamiento.getFoto(), e);
                }
            }

            alojamientoRepository.delete(alojamiento);
        });

        return true;
    }
    
}