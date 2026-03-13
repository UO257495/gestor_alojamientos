package com.nayarasanchez.gestor_alojamientos.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nayarasanchez.gestor_alojamientos.dto.form.ReservaForm;
import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
import com.nayarasanchez.gestor_alojamientos.model.EstadoPago;
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
        return reservaRepository.findByClienteIdOrderByIdDesc(id);
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
        reserva.setEstadoPago(form.getEstadoPago());

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
        reserva.setEstadoPago(EstadoPago.PENDIENTE);

        
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

    public void cancelarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
    }

    /**
     * Calcula el precio total de una reserva por NOCHES según las fechas y el alojamiento.
     */
    /**
     * Calcula el precio total de una reserva calculando NOCHE a NOCHE.
     */
    public double calcularTotal(Long alojamientoId, LocalDate inicio, LocalDate fin) {
        // Validación: El check-out debe ser posterior al check-in
        if (alojamientoId == null || inicio == null || fin == null || !fin.isAfter(inicio)) {
            return 0.0;
        }

        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        double precioBase = alojamiento.getTarifaBase(); 
        double precioTotal = 0.0;

        // Iteramos desde el check-in hasta el día ANTES del check-out
        for (LocalDate noche = inicio; noche.isBefore(fin); noche = noche.plusDays(1)) {
            
            // Buscamos si ESA noche en concreto tiene precio de temporada
            // Al pasar (noche, noche), el repositorio busca si esa fecha exacta cae en temporada
            Double precioTemporada = temporadaRepository
                    .findPrecioPorFechasYAlojamiento(noche, noche, alojamientoId)
                    .orElse(0.0);

            // Sumamos la noche al total
            precioTotal += (precioBase + precioTemporada);
        }

        return precioTotal;
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

    public List<Reserva> obtenerReservasPorAnio(int anio) {
        LocalDate inicio = LocalDate.of(anio, 1, 1);
        LocalDate fin = LocalDate.of(anio, 12, 31);
        return reservaRepository.findByFechaInicioBetween(inicio, fin);
    }

    public List<Integer> obtenerAniosConDatos() {
        return reservaRepository.findDistinctAnios(); // consulta JPQL personalizada
    }

    public Map<Integer, Double> calcularOcupacionMensual(int anio, List<Reserva> reservas, int totalAlojamientos) {
        return IntStream.rangeClosed(1, 12)
            .boxed()
            .collect(Collectors.toMap(
                mes -> mes,
                mes -> {
                    YearMonth ym = YearMonth.of(anio, mes);
                    int diasMes = ym.lengthOfMonth();

                    // Inicializamos todos los días del mes a 0 ocupación
                    Map<LocalDate, Long> ocupacionPorDia = IntStream.rangeClosed(1, diasMes)
                            .mapToObj(ym::atDay)
                            .collect(Collectors.toMap(d -> d, d -> 0L));

                    for (Reserva r : reservas) {
                        // Iteramos noche a noche (usando isBefore excluye el día de salida)
                        for (LocalDate noche = r.getFechaInicio(); noche.isBefore(r.getFechaFin()); noche = noche.plusDays(1)) {
                            
                            // Si la noche en la que duerme cae en el mes y año que estamos calculando, la contamos
                            if (noche.getMonthValue() == mes && noche.getYear() == anio) {
                                ocupacionPorDia.put(noche, ocupacionPorDia.get(noche) + 1);
                            }
                        }
                    }

                    // Calculamos el promedio de ocupación de esas noches en porcentaje
                    if (totalAlojamientos == 0) return 0.0; // Evitar división por cero
                    
                    return ocupacionPorDia.values().stream()
                            .mapToDouble(c -> (c * 100.0) / totalAlojamientos)
                            .average()
                            .orElse(0.0);
                }
            ));
    }


}

