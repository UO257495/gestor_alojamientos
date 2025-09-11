package com.nayarasanchez.gestor_alojamientos.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nayarasanchez.gestor_alojamientos.dto.form.AlojamientoForm;
import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
import com.nayarasanchez.gestor_alojamientos.repository.AlojamientoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlojamientoService {

    private final AlojamientoRepository alojamientoRepository;
    private final S3Service s3Service;

     /**
     * Lista todos los alojamientos registrados
     */
    public List<Alojamiento> listarTodos() {
        return alojamientoRepository.findAll();
    }

    /**
     * Busca un alojamiento por su ID
     */
    public Optional<Alojamiento> buscarPorId(Long id) {
        return alojamientoRepository.findById(id);
    }

    /**
     * Crea o actualiza un alojamiento
     */
    public Alojamiento crearOActualizar(AlojamientoForm form) throws IOException {
        Alojamiento alojamiento;

        // Si existe el id → actualizar, si no → crear nuevo
        if (form.getId() != null) {
            alojamiento = alojamientoRepository.findById(form.getId())
                    .orElse(new Alojamiento());
        } else {
            alojamiento = new Alojamiento();
        }

        // Mapear datos del formulario
        alojamiento.setNombre(form.getNombre());
        alojamiento.setDescripcion(form.getDescripcion());
        alojamiento.setDireccion(form.getDireccion());
        alojamiento.setLatitud(form.getLatitud());
        alojamiento.setLongitud(form.getLongitud());
        alojamiento.setTarifaBase(form.getTarifaBase());
        alojamiento.setPropietario(null);
        //TODO: ASIGNAR EL PROPIETARIO REAL

        // Subir foto a S3 si se ha cargado
        MultipartFile foto = form.getFoto();
        if (foto != null && !foto.isEmpty()) {
            String url = s3Service.uploadFile(foto);
            alojamiento.setFoto(url);
        }

        // Guardar en base de datos
        return alojamientoRepository.save(alojamiento);
    }

}