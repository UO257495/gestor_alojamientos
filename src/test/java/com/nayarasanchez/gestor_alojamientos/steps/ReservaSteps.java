package com.nayarasanchez.gestor_alojamientos.steps;

import com.nayarasanchez.gestor_alojamientos.dto.form.ReservaForm;
import com.nayarasanchez.gestor_alojamientos.model.Alojamiento;
import com.nayarasanchez.gestor_alojamientos.model.EstadoPago;
import com.nayarasanchez.gestor_alojamientos.model.EstadoReserva;
import com.nayarasanchez.gestor_alojamientos.model.Reserva;
import com.nayarasanchez.gestor_alojamientos.model.Temporada;
import com.nayarasanchez.gestor_alojamientos.dto.form.TemporadaForm;
import com.nayarasanchez.gestor_alojamientos.service.TemporadaService;
import com.nayarasanchez.gestor_alojamientos.model.Usuario;
import com.nayarasanchez.gestor_alojamientos.repository.AlojamientoRepository;
import com.nayarasanchez.gestor_alojamientos.repository.ReservaRepository;
import com.nayarasanchez.gestor_alojamientos.repository.TemporadaRepository;
import com.nayarasanchez.gestor_alojamientos.repository.UsuarioRepository;
import com.nayarasanchez.gestor_alojamientos.service.EmailService;
import com.nayarasanchez.gestor_alojamientos.service.ReservaService;

import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ReservaSteps {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private AlojamientoRepository alojamientoRepository;

    @Autowired
    private TemporadaRepository temporadaRepository;

    @Autowired
    private TemporadaService temporadaService;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @MockBean
    private EmailService emailService;

    private Alojamiento alojamiento;
    private Usuario cliente;
    private double total;
    private long noches;
    private Reserva reservaCreada;
    private Exception exceptionLanzada;
    private Temporada temporadaCreada;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Before
    public void limpiarDatos() {
        reservaRepository.deleteAll();
        temporadaRepository.deleteAll();
        alojamientoRepository.deleteAll();
        usuarioRepository.deleteAll();

        total = 0.0;
        noches = 0;
        alojamiento = null;
        cliente = null;
        reservaCreada = null;
        exceptionLanzada = null;
        temporadaCreada = null;
    }

    @Dado("que existe un alojamiento {string} con una tarifa base de {double} euros")
    public void crearAlojamiento(String nombre, Double tarifa) {
        Alojamiento a = new Alojamiento();
        a.setNombre(nombre);
        a.setTarifaBase(tarifa);
        a.setDireccion("Test");
        a.setCapacidad(2);
        a.setLatitud(0.0);
        a.setLongitud(0.0);

        alojamiento = alojamientoRepository.save(a);
    }

    @Dado("existe una temporada {string} para ese alojamiento desde el {string} hasta el {string} con un suplemento de {double} euros")
    public void crearTemporada(String nombre, String inicio, String fin, Double suplemento) {
        Temporada temporada = new Temporada();
        temporada.setNombre(nombre);
        temporada.setFechaInicio(LocalDate.parse(inicio, formatter));
        temporada.setFechaFin(LocalDate.parse(fin, formatter));
        temporada.setPrecio(suplemento);
        temporada.setAlojamiento(alojamiento);

        temporadaRepository.save(temporada);
    }

    @Dado("existe un cliente {string} con email {string} y dni {string}")
    public void crearCliente(String nombre, String email, String dni) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setDni(dni);
        usuario.setTelefono("600000000");
        usuario.setPassword("1234");

        cliente = usuarioRepository.save(usuario);
    }

    @Dado("existe una reserva confirmada para ese alojamiento desde el {string} hasta el {string}")
    public void crearReservaConfirmada(String inicio, String fin) {
        crearReservaConEstado(inicio, fin, EstadoReserva.CONFIRMADA);
    }

    @Dado("existe una reserva cancelada para ese alojamiento desde el {string} hasta el {string}")
    public void crearReservaCancelada(String inicio, String fin) {
        crearReservaConEstado(inicio, fin, EstadoReserva.CANCELADA);
    }

    @Dado("existe una reserva rechazada para ese alojamiento desde el {string} hasta el {string}")
    public void crearReservaRechazada(String inicio, String fin) {
        crearReservaConEstado(inicio, fin, EstadoReserva.RECHAZADA);
    }

    @Dado("existe una reserva pendiente para ese alojamiento desde el {string} hasta el {string}")
    public void crearReservaPendiente(String inicio, String fin) {
        crearReservaConEstado(inicio, fin, EstadoReserva.PENDIENTE);
    }

    private void crearReservaConEstado(String inicio, String fin, EstadoReserva estado) {
        Reserva reserva = new Reserva();
        reserva.setAlojamiento(alojamiento);
        reserva.setCliente(cliente);
        reserva.setFechaInicio(LocalDate.parse(inicio, formatter));
        reserva.setFechaFin(LocalDate.parse(fin, formatter));
        reserva.setEstado(estado);
        reserva.setEstadoPago(EstadoPago.PENDIENTE);
        reserva.setFormaPago(null);
        reserva.setPrecioTotal(0.0);

        reservaRepository.save(reserva);
    }

    @Cuando("calculo el total de la reserva desde el {string} hasta el {string}")
    public void calcularTotal(String inicio, String fin) {
        LocalDate fechaInicio = LocalDate.parse(inicio, formatter);
        LocalDate fechaFin = LocalDate.parse(fin, formatter);

        noches = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        total = reservaService.calcularTotal(alojamiento.getId(), fechaInicio, fechaFin);
    }

    @Cuando("intento crear una reserva de cliente para ese alojamiento desde el {string} hasta el {string}")
    public void intentoCrearReservaCliente(String inicio, String fin) {
        try {
            ReservaForm form = new ReservaForm();
            form.setAlojamientoId(alojamiento.getId());
            form.setClienteId(cliente.getId());
            form.setFechaInicio(LocalDate.parse(inicio, formatter));
            form.setFechaFin(LocalDate.parse(fin, formatter));
            form.setPrecioTotal(1.0);

            reservaCreada = reservaService.crearReservaCliente(form);
        } catch (Exception e) {
            exceptionLanzada = e;
        }
    }

    @Entonces("el sistema debe calcular {int} noches")
    public void comprobarNoches(int esperado) {
        assertEquals(esperado, noches);
    }

    @Entonces("el precio total debe ser {double} euros")
    public void comprobarPrecio(double esperado) {
        assertEquals(esperado, total, 0.01);
    }

    @Entonces("el sistema debe indicar que las fechas se solapan")
    public void comprobarSolapamiento() {
        assertNotNull(exceptionLanzada);
        assertTrue(exceptionLanzada.getMessage().contains("solapan"));
    }

    @Entonces("la reserva debe ser válida")
    public void comprobarReservaValida() {
        assertNull(exceptionLanzada);
        assertNotNull(reservaCreada);
    }

    @Cuando("un cliente crea una reserva para ese alojamiento desde el {string} hasta el {string} con un precio enviado de {double} euros")
    public void clienteCreaReservaConPrecioManipulado(String inicio, String fin, Double precioEnviado) {
        try {
            ReservaForm form = new ReservaForm();
            form.setAlojamientoId(alojamiento.getId());
            form.setClienteId(cliente.getId());
            form.setFechaInicio(LocalDate.parse(inicio, formatter));
            form.setFechaFin(LocalDate.parse(fin, formatter));
            form.setPrecioTotal(precioEnviado);

            reservaCreada = reservaService.crearReservaCliente(form);
        } catch (Exception e) {
            exceptionLanzada = e;
        }
    }

    @Entonces("la reserva se guarda con estado {string}")
    public void comprobarEstadoReserva(String estadoEsperado) {
        assertNotNull(reservaCreada);
        assertEquals(estadoEsperado, reservaCreada.getEstado().name());
    }

    @Entonces("la reserva se guarda con precio total de {double} euros")
    public void comprobarPrecioReservaGuardada(Double precioEsperado) {
        assertNotNull(reservaCreada);
        assertEquals(precioEsperado, reservaCreada.getPrecioTotal(), 0.01);
    }


    @Cuando("intento crear una reserva de cliente inválida para ese alojamiento desde el {string} hasta el {string}")
    public void intentoCrearReservaInvalida(String inicio, String fin) {
        try {
            ReservaForm form = new ReservaForm();
            form.setAlojamientoId(alojamiento.getId());
            form.setClienteId(cliente.getId());
            form.setFechaInicio(LocalDate.parse(inicio, formatter));
            form.setFechaFin(LocalDate.parse(fin, formatter));
            form.setPrecioTotal(1.0);

            reservaCreada = reservaService.crearReservaCliente(form);
        } catch (Exception e) {
            exceptionLanzada = e;
        }
    }

    @Entonces("el sistema debe indicar que la fecha de fin debe ser posterior a la de inicio")
    public void comprobarErrorFechasInvalidas() {
        assertNotNull(exceptionLanzada);
        assertTrue(exceptionLanzada.getMessage().contains("fecha de fin debe ser posterior"));
    }


    @Cuando("creo una temporada {string} para ese alojamiento desde el {string} hasta el {string} con un suplemento de {double} euros")
    public void creoUnaTemporada(String nombre, String inicio, String fin, Double suplemento) {
        try {
            TemporadaForm form = new TemporadaForm();
            form.setNombre(nombre);
            form.setFechaInicio(LocalDate.parse(inicio, formatter));
            form.setFechaFin(LocalDate.parse(fin, formatter));
            form.setPrecio(suplemento);
            form.setAlojamiento(alojamiento.getId());

            temporadaCreada = temporadaService.crearOActualizar(form);
        } catch (Exception e) {
            exceptionLanzada = e;
        }
    }

    @Entonces("la temporada debe crearse correctamente")
    public void laTemporadaDebeCrearseCorrectamente() {
        assertNull(exceptionLanzada);
        assertNotNull(temporadaCreada);
    }

    @Entonces("el sistema debe indicar que la temporada se solapa")
    public void elSistemaDebeIndicarQueLaTemporadaSeSolapa() {
        assertNotNull(exceptionLanzada);
        assertTrue(exceptionLanzada.getMessage().contains("solapa"));
    }
}