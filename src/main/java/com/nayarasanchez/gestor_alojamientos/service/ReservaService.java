package com.nayarasanchez.gestor_alojamientos.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
    private final EmailService emailService;
    

    public List<Reserva> listarTodas() {
        return reservaRepository.findAll();
    }


    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepository.findById(id);
    }

    public List<Reserva> buscarPorClienteId(Long id) {
        return reservaRepository.findByClienteIdOrderByIdDesc(id);
    }

    public Reserva crearOActualizar(ReservaForm form) {
        return guardarReserva(form, form.getEstado(), form.getEstadoPago());
    }

    public Reserva crearReservaCliente(ReservaForm form) {
        return guardarReserva(form, EstadoReserva.PENDIENTE, EstadoPago.PENDIENTE);
    }


    private Reserva guardarReserva(ReservaForm form, EstadoReserva estado, EstadoPago estadoPago) {
            validarFechas(form);

            Reserva reserva = obtenerReserva(form.getId());

            Usuario cliente = usuarioRepository.findById(form.getClienteId()).orElseThrow();
            Alojamiento alojamiento = alojamientoRepository.findById(form.getAlojamientoId()).orElseThrow();

            validarSolapamiento(form);

            double total = calcularTotal(
                form.getAlojamientoId(),
                form.getFechaInicio(),
                form.getFechaFin()
            );

            rellenarDatosReserva(reserva, form, cliente, alojamiento, estado, estadoPago, total);

            Reserva reservaGuardada = reservaRepository.save(reserva);

            enviarEmailReserva(cliente, alojamiento, form, estado, total);

            return reservaGuardada;
        }

        private static final Map<EstadoReserva, String> PLANTILLAS_ESTADO = Map.of(
            EstadoReserva.CANCELADA, "ha sido CANCELADA.",
            EstadoReserva.RECHAZADA, "se encuentra en estado RECHAZADA.",
            EstadoReserva.PENDIENTE, "se encuentra en estado PENDIENTE.",
            EstadoReserva.CONFIRMADA, "ha sido CONFIRMADA.\nEsperamos con gusto su visita."
        );

        private void enviarEmailReserva(
            Usuario cliente,
            Alojamiento alojamiento,
            ReservaForm form,
            EstadoReserva estado,
            double total) {

        String asunto = String.format(
            "Confirmación de reserva en %s",
            alojamiento.getNombre()
        );

        String textoEstado = PLANTILLAS_ESTADO.getOrDefault(
            estado,
            "ha sido recibida."
        );

        String mensaje = String.format("""
            Hola %s,

            Tu petición de reserva del %s al %s %s

            Precio total: %.2f €.

            Forma de pago: %s.

            Si su forma de pago ha sido mediante transferencia, debe ingresar la cantidad correspondiente en la cuenta ES95 4433 7788 9855 5252 6644.
            Si no, se le cobrará directamente en el alojamiento a su llegada.

            Gracias por confiar en nosotros.
            """,
            cliente.getNombre(),
            form.getFechaInicio(),
            form.getFechaFin(),
            textoEstado,
            total,
            form.getFormaPago()
        );

        emailService.enviarCorreo(cliente.getEmail(), asunto, mensaje);
    }

    private void validarFechas(ReservaForm form) {
        if (form.getFechaInicio() == null 
                || form.getFechaFin() == null 
                || !form.getFechaFin().isAfter(form.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
    }

    private Reserva obtenerReserva(Long id) {
        if (id != null) {
            return reservaRepository.findById(id).orElse(new Reserva());
        }
        return new Reserva();
    }

    private void validarSolapamiento(ReservaForm form) {
        if (existeSolapamiento(
                form.getAlojamientoId(),
                form.getFechaInicio(),
                form.getFechaFin(),
                form.getId())) {
            throw new IllegalArgumentException("Las fechas de la reserva se solapan con otra existente");
        }
    }

    private void rellenarDatosReserva(
            Reserva reserva,
            ReservaForm form,
            Usuario cliente,
            Alojamiento alojamiento,
            EstadoReserva estado,
            EstadoPago estadoPago,
            double total) {

        reserva.setFechaInicio(form.getFechaInicio());
        reserva.setFechaFin(form.getFechaFin());
        reserva.setEstado(estado);
        reserva.setCliente(cliente);
        reserva.setAlojamiento(alojamiento);
        reserva.setPrecioTotal(total);
        reserva.setFormaPago(form.getFormaPago());
        reserva.setEstadoPago(estadoPago);
    }

    private boolean existeSolapamiento(Long alojamientoId, LocalDate inicio, LocalDate fin, Long reservaIdActual) {
        List<Reserva> reservas = reservaRepository.findByAlojamientoIdAndEstadoNotIn(
            alojamientoId,
            List.of(EstadoReserva.CANCELADA, EstadoReserva.RECHAZADA)
        );

        return reservas.stream()
            .filter(r -> reservaIdActual == null || !r.getId().equals(reservaIdActual))
            .anyMatch(r -> inicio.isBefore(r.getFechaFin()) && fin.isAfter(r.getFechaInicio()));
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


    public double calcularTotal(Long alojamientoId, LocalDate inicio, LocalDate fin) {
        if (alojamientoId == null || inicio == null || fin == null || !fin.isAfter(inicio)) {
            return 0.0;
        }

        Alojamiento alojamiento = alojamientoRepository.findById(alojamientoId)
                .orElseThrow(() -> new IllegalArgumentException("Alojamiento no encontrado"));

        double precioBase = alojamiento.getTarifaBase(); 
        double precioTotal = 0.0;

        for (LocalDate noche = inicio; noche.isBefore(fin); noche = noche.plusDays(1)) {
            
            Double precioTemporada = temporadaRepository
                    .findPrecioPorFechasYAlojamiento(noche, noche, alojamientoId)
                    .orElse(0.0);

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
        return reservaRepository.findDistinctAnios(); 
    }

    public Map<Integer, Double> calcularOcupacionMensual(int anio, List<Reserva> reservas, int totalAlojamientos) {
        return IntStream.rangeClosed(1, 12)
            .boxed()
            .collect(Collectors.toMap(
                mes -> mes,
                mes -> {
                    YearMonth ym = YearMonth.of(anio, mes);
                    int diasMes = ym.lengthOfMonth();

                    Map<LocalDate, Long> ocupacionPorDia = IntStream.rangeClosed(1, diasMes)
                            .mapToObj(ym::atDay)
                            .collect(Collectors.toMap(d -> d, d -> 0L));

                    for (Reserva r : reservas) {
                        for (LocalDate noche = r.getFechaInicio(); noche.isBefore(r.getFechaFin()); noche = noche.plusDays(1)) {
                            
                            if (noche.getMonthValue() == mes && noche.getYear() == anio) {
                                ocupacionPorDia.put(noche, ocupacionPorDia.get(noche) + 1);
                            }
                        }
                    }


                    if (totalAlojamientos == 0) return 0.0; 
                    
                    return ocupacionPorDia.values().stream()
                            .mapToDouble(c -> (c * 100.0) / totalAlojamientos)
                            .average()
                            .orElse(0.0);
                }
            ));
    }


}

