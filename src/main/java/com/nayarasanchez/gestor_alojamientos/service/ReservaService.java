package com.nayarasanchez.gestor_alojamientos.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nayarasanchez.gestor_alojamientos.dto.form.ReservaForm;
import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
import com.nayarasanchez.gestor_alojamientos.model.EstadoReserva;
import com.nayarasanchez.gestor_alojamientos.model.Reserva;
import com.nayarasanchez.gestor_alojamientos.model.Usuario;
import com.nayarasanchez.gestor_alojamientos.repository.AlojamientoRepository;
import com.nayarasanchez.gestor_alojamientos.repository.ReservaRepository;
import com.nayarasanchez.gestor_alojamientos.repository.TemporadaRepository;
import com.nayarasanchez.gestor_alojamientos.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservaService {
    
    private final ReservaRepository reservaRepository;
    private final AlojamientoRepository alojamientoRepository;
    private final TemporadaRepository temporadaRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;
    
    /**
     * Lista todas las reservas registrados
     */
    public List<Reserva> listarTodas() {
        return reservaRepository.findAll();
    }

    /**
     * Busca un reserva por su ID
     */
    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepository.findById(id);
    }

     /**
     * Busca un reserva por email del cliente
     */
    public List<Reserva> buscarPorClienteId(Long id) {
        return reservaRepository.findByClienteId(id);
    }

    
    /**
     * Crea o actualiza una reserva del propietario
     */
    public Reserva crearOActualizar(ReservaForm form) {
        Reserva reserva = (form.getId() != null)
                ? reservaRepository.findById(form.getId()).orElse(new Reserva())
                : new Reserva();

        reserva.setFechaInicio(form.getFechaInicio());
        reserva.setFechaFin(form.getFechaFin());
        reserva.setEstado(form.getEstado());

        Usuario cliente = usuarioRepository.findById(form.getClienteId()).orElseThrow();
        reserva.setCliente(cliente);

        Alojamiento alojamiento = alojamientoRepository.findById(form.getAlojamientoId()).orElseThrow();
        reserva.setAlojamiento(alojamiento);

        reserva.setPrecioTotal(form.getPrecioTotal());
        reserva.setFormaPago(form.getFormaPago());

        //Envio de email 
         String asunto = "Confirmación de reserva en " + alojamiento.getNombre();
         String mensaje = ""; 

         if(form.getEstado().equals(EstadoReserva.CANCELADA)){
            mensaje = "Hola " + cliente.getNombre() + ",\n\n"
                + "Tu petición de reserva del "
                + form.getFechaInicio() + " al " + form.getFechaFin() + " ha sido CANCELADA." + ".\n\n"
                + "Precio total: " + form.getPrecioTotal() + " €.\n\n"
                + "Forma de pago: " + form.getFormaPago() + ".\n\n"
                + "Si su forma de pago ha sido mediante transferencia, debe ingresar la cantidad correspondiente en la cuenta ES95 4433 7788 9855 5252 6644." 
                + "Si no, se le cobrará directamente en el alojamiento a su llegada"+ ".\n\n"
                + "Gracias por confiar en nosotros.\n\n";
            emailService.enviarCorreo(cliente.getEmail(), asunto, mensaje);
         }else if(form.getEstado().equals(EstadoReserva.RECHAZADA)){
            mensaje = "Hola " + cliente.getNombre() + ",\n\n"
                + "Tu petición de reserva del "
                + form.getFechaInicio() + " al " + form.getFechaFin() + " se encuentra en estado RECHAZADA." + ".\n\n"
                + "Precio total: " + form.getPrecioTotal() + " €.\n\n"
                + "Forma de pago: " + form.getFormaPago() + ".\n\n"
                + "Si su forma de pago ha sido mediante transferencia, debe ingresar la cantidad correspondiente en la cuenta ES95 4433 7788 9855 5252 6644." 
                + "Si no, se le cobrará directamente en el alojamiento a su llegada"+ ".\n\n"
                + "Gracias por confiar en nosotros.\n\n";
            emailService.enviarCorreo(cliente.getEmail(), asunto, mensaje);
         }else if(form.getEstado().equals(EstadoReserva.PENDIENTE)){
            mensaje = "Hola " + cliente.getNombre() + ",\n\n"
                + "Tu petición de reserva del "
                + form.getFechaInicio() + " al " + form.getFechaFin() + " se encuentra en estado PENDIENTE." + ".\n\n"
                + "Precio total: " + form.getPrecioTotal() + " €.\n\n"
                + "Forma de pago: " + form.getFormaPago() + ".\n\n"
                + "Si su forma de pago ha sido mediante transferencia, debe ingresar la cantidad correspondiente en la cuenta ES95 4433 7788 9855 5252 6644." 
                + "Si no, se le cobrará directamente en el alojamiento a su llegada"+ ".\n\n"
                + "Gracias por confiar en nosotros.\n\n";
            emailService.enviarCorreo(cliente.getEmail(), asunto, mensaje);
         }else if(form.getEstado().equals(EstadoReserva.CONFIRMADA)){
            mensaje = "Hola " + cliente.getNombre() + ",\n\n"
                + "Tu petición de reserva del "
                + form.getFechaInicio() + " al " + form.getFechaFin() + " ha sido CONFIRMADA."  + ".\n"
                + "Esperamos con gusto su visita" + ".\n\n"
                + "Precio total: " + form.getPrecioTotal() + " €.\n\n"
                + "Forma de pago: " + form.getFormaPago() + ".\n\n"
                + "Si su forma de pago ha sido mediante transferencia, debe ingresar la cantidad correspondiente en la cuenta ES95 4433 7788 9855 5252 6644." 
                + "Si no, se le cobrará directamente en el alojamiento a su llegada"+ ".\n\n"
                + "Gracias por confiar en nosotros.\n\n";
            emailService.enviarCorreo(cliente.getEmail(), asunto, mensaje);
         }

        Reserva reservaGuardada = reservaRepository.save(reserva);

        return reservaGuardada;
    }

        /**
     * Crea o actualiza una reserva de un cliente
     */
    public Reserva crearReservaCliente(ReservaForm form) {
        Reserva reserva = (form.getId() != null)
                ? reservaRepository.findById(form.getId()).orElse(new Reserva())
                : new Reserva();

        reserva.setFechaInicio(form.getFechaInicio());
        reserva.setFechaFin(form.getFechaFin());
        reserva.setEstado(EstadoReserva.PENDIENTE);

        Usuario cliente = usuarioRepository.findById(form.getClienteId()).orElseThrow();
        reserva.setCliente(cliente);

        Alojamiento alojamiento = alojamientoRepository.findById(form.getAlojamientoId()).orElseThrow();
        reserva.setAlojamiento(alojamiento);

        reserva.setPrecioTotal(form.getPrecioTotal());
        reserva.setFormaPago(form.getFormaPago());

        
        Reserva reservaGuardada = reservaRepository.save(reserva);

        //Enviar email de recepción
        String asunto = "Confirmación de reserva en " + alojamiento.getNombre();
        String mensaje = "Hola " + cliente.getNombre() + ",\n\n"
                + "Tu petición de reserva del "
                + form.getFechaInicio() + " al " + form.getFechaFin() + " ha sido recibida." + ".\n"
                + "Recibirás otro email con la confirmación" + ".\n\n"
                + "Precio total: " + form.getPrecioTotal() + " €.\n\n"
                + "Forma de pago: " + form.getFormaPago() + ".\n\n"
                + "Si su forma de pago ha sido mediante transferencia, debe ingresar la cantidad correspondiente en la cuenta ES95 4433 7788 9855 5252 6644." 
                + "Si no, se le cobrará directamente en el alojamiento a su llegada"+ ".\n\n"
                + "Gracias por confiar en nosotros.\n\n";

        emailService.enviarCorreo(cliente.getEmail(), asunto, mensaje);

        return reservaGuardada;
    }

    public void eliminar(Long id) {
        reservaRepository.deleteById(id);
    }

    /**
     * Calcula el precio total de una reserva según las fechas y el alojamiento.
     */
    public double calcularTotal(Long alojamientoId, LocalDate inicio, LocalDate fin) {
        if (alojamientoId == null || inicio == null || fin == null || fin.isBefore(inicio)) {
            return 0.0;
        }

        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        double precioBase = alojamiento.getTarifaBase(); 

        // Buscar la temporada activa en esas fechas
        Double precioTemporada = temporadaRepository
                .findPrecioPorFechasYAlojamiento(inicio, fin, alojamientoId)
                .orElse(0.0);

        long dias = ChronoUnit.DAYS.between(inicio, fin);
        if (dias <= 0) dias = 1; // al menos 1 día

        return (precioBase + precioTemporada) * dias;
    }

    public List<Reserva> findFechasOcupadas(Long alojamientoId) {
        return reservaRepository.findByAlojamientoIdAndEstadoNotIn(
            alojamientoId,
            List.of(EstadoReserva.CANCELADA, EstadoReserva.RECHAZADA)
        );
    }

    public Collection<Reserva> findByAlojamientoId(Long id) {
        return reservaRepository.findByAlojamientoId(id);
    }

}

